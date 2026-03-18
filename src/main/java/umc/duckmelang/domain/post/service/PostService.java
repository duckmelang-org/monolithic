package umc.duckmelang.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostDto;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;

@Service
@RequiredArgsConstructor
public class PostService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostViewCountService postViewCountService;

    public Post addPost(PostDto.PostAddDto request, Long memberId){
        Member member = getMemberOrThrow(memberId);
        Post post = PostConverter.toPost(request, member);
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostList(int page, int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PostDto.PostDetailDto getPost(Long postId){
        Post post = getPostOrThrow(postId);
        postViewCountService.increment(postId);
        long redisViewCount = postViewCountService.getViewCount(postId);
        return PostConverter.toPostDetailDto(post, redisViewCount);
    }

    @Cacheable(value = "popularPosts", key = "#page + '_' + #size")
    @Transactional(readOnly = true)
    public PostDto.PostPopularListDto getPopularPosts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllOrderByViewCountDesc(pageable);
        return PostConverter.toPostPopularListDto(postPage);
    }

    private Post getPostOrThrow(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(()-> new PostException(ErrorStatus.POST_NOT_FOUND));
    }

    private Member getMemberOrThrow(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }
}
