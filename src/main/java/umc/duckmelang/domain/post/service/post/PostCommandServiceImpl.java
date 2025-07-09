package umc.duckmelang.domain.post.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.eventcategory.repository.EventCategoryRepository;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.repository.IdolCategoryRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.domain.PostImage;
import umc.duckmelang.domain.post.dto.PostRequestDto;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.post.domain.PostIdol;
import umc.duckmelang.domain.post.repository.PostIdolRepository;
import umc.duckmelang.domain.post.repository.PostImageRepository;
import umc.duckmelang.domain.uuid.service.UuidService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.EventCategoryException;
import umc.duckmelang.global.apipayload.exception.IdolCategoryException;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;
import umc.duckmelang.global.aws.AmazonS3Manager;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandServiceImpl implements PostCommandService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final IdolCategoryRepository idolCategoryRepository;
    private final UuidService uuidService;
    private final PostImageRepository postImageRepository;
    private final PostIdolRepository postIdolRepository;
    private final AmazonS3Manager s3Manager;

    // 게시글과 이미지 업로드 처리
    private void savePostImages(Post post, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            String uuid = uuidService.generateUniqueUuidString();

            String imageUrl = s3Manager.uploadFile(s3Manager.generatePostImageKeyName(uuid), file);
            postImageRepository.save(
                    PostImage.builder()
                            .post(post)
                            .postImageUrl(imageUrl)
                            .uuid(uuid)
                    .build());
        }
    }

    @Override
    public Post joinPost(PostRequestDto.PostJoinDto request, Long memberId) {
//        Member 엔티티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

//        EventCategory 엔티티 조회
        EventCategory eventCategory = eventCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EventCategoryException(ErrorStatus.EVENT_CATEGORY_NOT_FOUND));

//        IdolCategory 엔티티 리스트 조회
        List<IdolCategory> idolCategories = idolCategoryRepository.findAllById(request.getIdolIds());
        if (idolCategories.isEmpty()) {
            throw new IdolCategoryException(ErrorStatus.IDOL_CATEGORY_NOT_FOUND);
        }
        Post newPost = PostConverter.toPost(request, member, eventCategory, idolCategories);
        return postRepository.save(newPost);
    }

    @Override
    public Post joinPost(PostRequestDto.PostJoinDto request, Long memberId, List<MultipartFile> postImages) {
        Post post = joinPost(request, memberId);

        // 이미지 업로드 처리
        if (postImages != null && !postImages.isEmpty()) {
            savePostImages(post, postImages);
        }

        return post;
    }

    @Override
    public Post patchPostStatus(Long postId, Short wanted) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));
        post.setWanted(wanted);

        return postRepository.save(post);
    }

    @Override
    public void deleteMyPost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostException(ErrorStatus.POST_NOT_FOUND));
        postRepository.delete(post);
    }

    @Override
    public Post patchPost(Long postId, PostRequestDto.PostJoinDto request, List<MultipartFile> images ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

        // 게시글 수정
        post.updatePost(request.getTitle(), request.getContent(),
                eventCategoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new EventCategoryException(ErrorStatus.EVENT_CATEGORY_NOT_FOUND)),
                request.getDate());

        // 기존 PostIdol 삭제
        List<PostIdol> existingPostIdols = new ArrayList<>(post.getPostIdolList()); // 복사본 생성
        for (PostIdol postIdol : existingPostIdols) {
            post.getPostIdolList().remove(postIdol);
            postIdolRepository.delete(postIdol);
        }

        List<IdolCategory> idolCategories = idolCategoryRepository.findAllById(request.getIdolIds());
        if (idolCategories.isEmpty()) {
            throw new IdolCategoryException(ErrorStatus.IDOL_CATEGORY_NOT_FOUND);
        }

        List<PostIdol> updatedPostIdols = idolCategories.stream()
                .map(idolCategory -> PostIdol.builder()
                        .post(post)
                        .idolCategory(idolCategory)
                        .build())
                .toList();

        post.getPostIdolList().addAll(updatedPostIdols);

        if (images != null && !images.isEmpty()) {
            postImageRepository.deleteAllByPost(post);
            savePostImages(post, images);  // 이미지 업로드 처리
        }

        return postRepository.save(post);

    }
}

