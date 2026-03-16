package umc.duckmelang.domain.application;

import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.facade.OptimisticLockApplicationFacade;
import umc.duckmelang.domain.application.repository.ApplicationRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.type.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.domain.type.PostStatus;
import umc.duckmelang.domain.post.repository.PostRepository;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
public class OptimisticLockTest {

    @Autowired private PostRepository postRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private OptimisticLockApplicationFacade optimisticLockFacade;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Long savedPostId;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();

        Member host = memberRepository.save(Member.builder()
                .nickname("작성자").loginId("host").password("1234").role(Role.USER).build());

        Post post = postRepository.save(Post.builder()
                .title("동행 구해요")
                .content("같이 가요")
                .maxParticipants(4)
                .currentParticipants(0)
                .postStatus(PostStatus.RECRUITING)
                .member(host)
                .build());
        savedPostId = post.getId();

        for (int i = 0; i < 1000; i++) {
            memberRepository.save(Member.builder()
                    .nickname("유저" + i).loginId("member" + i).password("1234").role(Role.USER).build());
        }
    }

    @Test
    @DisplayName("[낙관적 락] 100명 동시 요청")
    void test100Threads() throws InterruptedException {
        runTest(100);
    }

    @Test
    @DisplayName("[낙관적 락] 1000명 동시 요청")
    void test1000Threads() throws InterruptedException {
        runTest(1000);
    }

    private void runTest(int threadCount) throws InterruptedException {
        List<Member> members = memberRepository.findAll();

        OperatingSystemMXBean osBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        AtomicReference<Double> peakCpu = new AtomicReference<>(0.0);
        ScheduledExecutorService cpuSampler = Executors.newSingleThreadScheduledExecutor();
        cpuSampler.scheduleAtFixedRate(() -> {
            double cpu = osBean.getProcessCpuLoad() * 100;
            peakCpu.updateAndGet(current -> Math.max(current, cpu));
        }, 0, 100, TimeUnit.MILLISECONDS);

        AtomicInteger retryCount = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(64);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            Long applicantId = members.get(i + 1).getId();
            executorService.submit(() -> {
                try {
                    optimisticLockFacade.createApplication(
                            new ApplicationRequestDto.CreateRequestDto(savedPostId), applicantId);
                } catch (Exception ignored) {
                    retryCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        long endTime = System.currentTimeMillis();

        cpuSampler.shutdown();

        double totalTime = (endTime - startTime) / 1000.0;

        Post post = postRepository.findById(savedPostId).orElseThrow();

        System.out.println("\n========= [낙관적 락] " + threadCount + "명 실험 결과 =========");
        System.out.println("처리 시간            : " + totalTime + "s");
        System.out.println("Throughput           : " + String.format("%.1f", threadCount / totalTime) + " 건/초");
        System.out.println("JVM CPU (peak)       : " + String.format("%.1f", peakCpu.get()) + "%");
        System.out.println("재시도 초과 실패 건수  : " + retryCount.get() + "건");
        System.out.println("최종 참가자 수 (정합성): " + post.getCurrentParticipants() + " / 4  ← 항상 4여야 정합");
        System.out.println("=====================================================\n");

        executorService.shutdown();
    }
}
