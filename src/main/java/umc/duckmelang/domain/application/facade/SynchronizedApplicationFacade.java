package umc.duckmelang.domain.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.service.ApplicationService;

@Component
@RequiredArgsConstructor
public class SynchronizedApplicationFacade {

    private final ApplicationService applicationService;

    /**
     * synchronized 블록이 @Transactional 커밋까지 감싸도록
     * 이 메서드 자체는 @Transactional을 붙이지 않는다.
     * applicationService.createApplication() 호출 시 트랜잭션이 시작·커밋된 뒤
     * synchronized 잠금이 해제된다.
     */
    public synchronized Application createApplication(
            ApplicationRequestDto.CreateRequestDto request, Long memberId) {
        return applicationService.createApplication(request, memberId);
    }
}