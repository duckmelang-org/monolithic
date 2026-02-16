package umc.duckmelang.domain.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.application.converter.ApplicationConverter;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.domain.type.Status;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.repository.ApplicationRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
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
    public Application createApplication(ApplicationRequestDto.CreateRequestDto request, Long memberId){

        Member member = memberRepository.findById(memberId).orElseThrow(()-> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        Post post = postRepository.findById(request.getPostId()).orElseThrow(()-> new PostException(ErrorStatus.POST_NOT_FOUND));
        Application application = ApplicationConverter.toApplication(post, member);
        return applicationRepository.save(application);
    }

    @Transactional
    public Application acceptApplication(Long applicationId, Long memberId){
        Application application = applicationRepository.findById(applicationId).orElseThrow(()-> new ApplicationException(ErrorStatus.APPLICATION_NOT_FOUND));

        if(!application.getPost().getMember().getId().equals(memberId)){
            throw new ApplicationException(ErrorStatus.APPLICATION_NOT_MATCH);
        }

        application.updateStatus(Status.ACCEPTED);

        return application;
    }
}
