package umc.duckmelang.domain.post.service;

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
import umc.duckmelang.domain.memberprofileimage.converter.MemberProfileImageConverter;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostRequestDto;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.postimage.converter.PostImageConverter;
import umc.duckmelang.domain.postimage.repository.PostImageRepository;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.domain.uuid.repository.UuidRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.EventCategoryException;
import umc.duckmelang.global.apipayload.exception.IdolCategoryException;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;
import umc.duckmelang.global.aws.AmazonS3Manager;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandServiceImpl implements PostCommandService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final IdolCategoryRepository idolCategoryRepository;
    private final UuidRepository uuidRepository;
    private final PostImageRepository postImageRepository;

    private final AmazonS3Manager s3Manager;

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

        for (MultipartFile file : postImages){
            String uuid = UUID.randomUUID().toString();
            Uuid savedUuid = uuidRepository.save(Uuid.builder()
                    .uuid(uuid).build());

            String imageUrl = s3Manager.uploadFile(s3Manager.generatePostImageKeyName(savedUuid), file);
            postImageRepository.save(PostImageConverter.toPostImage(post, imageUrl));
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
}

