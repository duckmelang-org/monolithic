package umc.duckmelang.domain.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.service.ApplicationService;
import umc.duckmelang.domain.application.repository.RedisLockRepository;

@Component
@RequiredArgsConstructor
public class LettuceLockApplicationFacade {

    private final RedisLockRepository redisLockRepository;
    private final ApplicationService applicationService;

    public void createApplicationWithLettuce(ApplicationRequestDto.CreateRequestDto request, Long memberId) throws InterruptedException {
        while (!redisLockRepository.lock(request.getPostId())) {
            Thread.sleep(100);
        }

        try {
            applicationService.createApplication(request, memberId);
        } finally {
            redisLockRepository.unlock(request.getPostId());
        }
    }
}
