package umc.duckmelang.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostDto;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public Post addPost(PostDto.PostAddDto request, Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

        Post post = PostConverter.toPost(request, member);

        return postRepository.save(post);
    }
}
