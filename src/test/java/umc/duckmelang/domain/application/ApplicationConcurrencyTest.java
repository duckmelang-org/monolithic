package umc.duckmelang.domain.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import umc.duckmelang.domain.application.dto.request.ApplicationRequestDto;
import umc.duckmelang.domain.application.repository.ApplicationRepository;
import umc.duckmelang.domain.application.service.ApplicationService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.type.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.domain.type.PostStatus;
import umc.duckmelang.domain.post.repository.PostRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ApplicationConcurrencyTest {

    @Autowired private ApplicationService applicationService;
    @Autowired private PostRepository postRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private MemberRepository memberRepository;

    private Long savePostId;

    @BeforeEach
    void setUp() {

        // 삭제
        applicationRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();

        // 게시글 작성자 생성
        Member member = memberRepository.save(Member.builder()
                .nickname("작성자").loginId("host").password("1234").role(Role.USER).build());

        // 테스트 게시글 생성
        Post post = postRepository.save(Post.builder()
                .title("동행 구해요")
                .content("같이 가요")
                .maxParticipants(4)
                .currentParticipants(0)
                .postStatus(PostStatus.RECRUITING)
                .member(member)
                .build());
        savePostId = post.getId();

        // 신청할 유저 100명 생성
        for (int i = 0; i < 100; i++){
            memberRepository.save(Member.builder()
                    .nickname("유저" + i).loginId("member" + i).password("1234").role(Role.USER).build());
        }
    }

    @Test
    @DisplayName("실험 1: 동시성 제어 없는 상태 측정")
    void multiThreadRequestTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        List<Member> members = memberRepository.findAll();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i ++){
            Long applicantId = members.get(i + 1).getId();
            executorService.submit(() -> {
                try{
                    applicationService.createApplication(new ApplicationRequestDto.CreateRequestDto(savePostId), applicantId);
                } catch (Exception e) {
                    // 에러 발생 시 로그 (Deadlock 등)
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;

        Post post = postRepository.findById(savePostId).orElseThrow();

        System.out.println("========= 실험 결과 =========");
        System.out.println("100명 처리 시간: " + totalTime + "s");
        System.out.println("최종 DB 기록 인원: " + post.getCurrentParticipants());
        System.out.println("Throughput (건/초): " + (threadCount / totalTime));
        System.out.println("===========================");

        assertEquals(4, post.getCurrentParticipants());
    }
}
