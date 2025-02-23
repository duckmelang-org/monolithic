package umc.duckmelang.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceImpl implements PostQueryService{
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    public PostResponseDto.PostPreviewListDto getFilteredPostList(Integer page, Gender gender, Integer minAge, Integer maxAge, Long memberId){
        Page<Post> postList;
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        if(gender == null && minAge == null && maxAge == null){
            postList = postRepository.findAll(PageRequest.of(page,10));
        } else{
            postList = postRepository.findFilteredPosts(gender, minAge, maxAge, member, PageRequest.of(page,10));
        }
        return PostConverter.postPreviewListDto(postList);
    }

    @Override
    public Page<Post> getFilteredPostListByIdol(Long idolId, Gender gender, Integer minAge, Integer maxAge, Integer page, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        return postRepository.findFilteredPostsByIdol(idolId, gender, minAge, maxAge, member, PageRequest.of(page, 10));
    }

    @Override
    public Page<Post> getPostListByMember(Long memberId, Integer page) {
        return postRepository.findByMember(memberId, PageRequest.of(page,10));
    }

    @Override
    public Optional<Post> getPostDetail(Long postId){

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));
        post.increaseViewCount();
        postRepository.save(post);
        return Optional.of(post);
    }

    @Override
    public Page<Post> getFilteredPostListByTitle(String keyword, Gender gender, Integer minAge, Integer maxAge, Integer page, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        return postRepository.findFilteredPostsByTitle(keyword, gender, minAge, maxAge, member, PageRequest.of(page, 10));
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public Page<Post> getMyPostList(Long memberId, Integer page){
        return postRepository.findMyPost(memberId, PageRequest.of(page, 10));
    }

    /**
     * 특정 멤버가 작성한 게시물 수 조회: 프로필 조회 시 사용
     *
     * @param memberId 멤버 ID
     * @return 게시물 수
     */
    @Override
    public int getPostCount(Long memberId) {
        return postRepository.countAllByMemberId(memberId);
    }
}
