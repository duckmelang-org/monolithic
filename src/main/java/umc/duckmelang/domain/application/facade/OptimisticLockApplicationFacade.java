package umc.duckmelang.domain.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.service.ApplicationService;

@Component
@RequiredArgsConstructor
public class OptimisticLockApplicationFacade {

    private static final int MAX_RETRY = 30;
    private static final long RETRY_DELAY_MS = 50;

    private final ApplicationService applicationService;

    /**
     * 낙관적 락: 충돌 시 ObjectOptimisticLockingFailureException 발생
     * → 재시도(retry)로 정합성을 보장한다.
     */
    public Application createApplication(
            ApplicationRequestDto.CreateRequestDto request, Long memberId) throws InterruptedException {

        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            try {
                return applicationService.createApplicationWithOptimisticLock(request, memberId);
            } catch (ObjectOptimisticLockingFailureException e) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new RuntimeException("낙관적 락 재시도 횟수 초과 (memberId=" + memberId + ")");
    }
}
