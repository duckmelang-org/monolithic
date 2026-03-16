package umc.duckmelang.domain.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.service.ApplicationService;

@Component
@RequiredArgsConstructor
public class NamedLockApplicationFacade {

    private static final int LOCK_TIMEOUT_SECONDS = 3;

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationService applicationService;

    /**
     * MySQL Named Lock 방식
     * - GET_LOCK(key, timeout): 락 획득 (실패 시 0 반환, timeout 초 대기)
     * - 비즈니스 로직은 별도 트랜잭션(@Transactional)에서 실행
     * - RELEASE_LOCK(key): finally 블록에서 반드시 해제
     *
     * Named Lock은 세션 단위로 관리되므로
     * 락 획득/해제와 트랜잭션 커밋이 같은 커넥션에서 이루어져야 함.
     * → Facade는 @Transactional 없이, ApplicationService가 자체 트랜잭션을 가짐.
     */
    public Application createApplication(
            ApplicationRequestDto.CreateRequestDto request, Long memberId) {

        String lockKey = "application:post:" + request.getPostId();

        try {
            Integer result = jdbcTemplate.queryForObject(
                    "SELECT GET_LOCK(?, ?)", Integer.class, lockKey, LOCK_TIMEOUT_SECONDS);
            if (result == null || result != 1) {
                throw new RuntimeException("Named Lock 획득 실패 (key=" + lockKey + ")");
            }
            return applicationService.createApplication(request, memberId);
        } finally {
            jdbcTemplate.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, lockKey);
        }
    }
}
