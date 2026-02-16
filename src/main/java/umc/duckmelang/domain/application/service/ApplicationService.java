package umc.duckmelang.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.application.converter.ApplicationConverter;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.domain.type.ApplicationStatus;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.repository.ApplicationRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.domain.type.PostStatus;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.ApplicationException;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public synchronized Application createApplication(ApplicationRequestDto.CreateRequestDto request, Long memberId){
        Member member = findMemberById(memberId);
        Post post = findPostById(request.getPostId());
        validateApplicationRequest(post, memberId);

        // 인원 증가 및 상태 업데이트
        post.incrementParticipants();
        if (post.getCurrentParticipants() >= post.getMaxParticipants()){
            post.updateStatus(PostStatus.CLOSED);
        }

        Application application = ApplicationConverter.toApplication(post, member);
        return applicationRepository.save(application);
    }

    @Transactional
    public Application acceptApplication(Long applicationId, Long memberId){
        Application application = findApplicationById(applicationId);
        validateHostAuthority(application.getPost(), memberId);

        application.updateStatus(ApplicationStatus.ACCEPTED);
        return application;
    }

    @Transactional
    public Application refuseApplication(Long applicationId, Long memberId){
        Application application = findApplicationById(applicationId);
        Post post = application.getPost();
        validateHostAuthority(application.getPost(), memberId);

        // 인원 감소 및 상태 업데이트
        post.decrementParticipants();
        application.updateStatus(ApplicationStatus.REJECTED);

        // 모집 재개
        if (post.getPostStatus() == PostStatus.CLOSED){
            post.updateStatus(PostStatus.RECRUITING);
        }

        return application;
    }

    private Member findMemberById(Long memberId){
        return memberRepository.findById(memberId).orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Post findPostById(Long postId){
        return postRepository.findById(postId).orElseThrow(()-> new PostException(ErrorStatus.POST_NOT_FOUND));
    }

    private Application findApplicationById(Long applicationId){
        return applicationRepository.findById(applicationId).orElseThrow(()-> new ApplicationException(ErrorStatus.APPLICATION_NOT_FOUND));
    }

    private void validateApplicationRequest(Post post, Long memberId){
        // 본인 게시글인지 확인
        if (post.getMember().getId().equals(memberId)){
            throw new ApplicationException(ErrorStatus.POST_SELF_APPLICATION);
        }

        // 신청 가능 상태인지 확인
        if (post.getPostStatus() == PostStatus.CLOSED){
            throw new ApplicationException(ErrorStatus.POST_FULL);
        }
    }

    private void validateHostAuthority(Post post, Long memberId) {
        if (!post.getMember().getId().equals(memberId)) {
            throw new ApplicationException(ErrorStatus.APPLICATION_NOT_MATCH);
        }
    }
}
