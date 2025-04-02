package umc.duckmelang.domain.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.application.domain.Application;
import umc.duckmelang.domain.application.dto.ShowApplicationDto;
import umc.duckmelang.domain.application.service.ApplicationQueryService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageQueryService;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostThumbnailResponseDto;
import umc.duckmelang.domain.post.service.PostImageQueryService;

@Service
@RequiredArgsConstructor

public class ApplicationFacadeService {
    private final ApplicationQueryService applicationQueryService;
    private final PostImageQueryService postImageQueryService;
    private final MemberProfileImageQueryService memberProfileImageQueryService;

    public Page<ShowApplicationDto> showReceivedPendingApplicationList(Long memberId, Integer page) {
        // 1. 동행요청 목록 조회
        Page<Application> applicationPage = applicationQueryService.getReceivedPendingApplicationList(memberId, page);

        // 2. Page.map()을 사용해 각 Application을 ShowApplicationDto로 변환
        return applicationPage.map(application -> {
            // 게시글 작성자 정보
            Post post = application.getPost();
            Member opposite = post.getMember();

            // 최신 이미지들 조회
            PostThumbnailResponseDto postImage = postImageQueryService.getLatestPostImage(post.getId());
            MemberProfileImage oppositeProfileImage = memberProfileImageQueryService
                    .getLatestPublicMemberProfileImage(opposite.getId()).orElse(MemberProfileImage.createDefault(opposite));

            // DTO 생성 및 반환
            return ShowApplicationDto.builder()
                    .postId(post.getId())
                    .postTitle(post.getTitle())
                    .postImage(postImage.getPostImageUrl())
                    .oppositeNickname(opposite.getNickname())
                    .oppositeProfileImage(oppositeProfileImage.getMemberImage())
                    .applicationId(application.getId())
                    .applicationCreatedAt(application.getCreatedAt())
                    .applicationStatus(application.getStatus())
                    .build();
        });
    }

    public Page<ShowApplicationDto> showReceivedApplicationListExceptPending(Long memberId, Integer page){
        // 1. 동행요청 목록 조회
        Page<Application> applicationPage = applicationQueryService.getReceivedApplicationListExceptPending(memberId, page);

        // 2. Page.map()을 사용해 각 Application을 ShowApplicationDto로 변환
        return applicationPage.map(application -> {
            // 게시글 작성자 정보
            Post post = application.getPost();
            Member opposite = post.getMember();

            // 최신 이미지들 조회
            PostThumbnailResponseDto postImage = postImageQueryService.getLatestPostImage(post.getId());
            MemberProfileImage oppositeProfileImage = memberProfileImageQueryService
                    .getLatestPublicMemberProfileImage(opposite.getId()).orElse(MemberProfileImage.createDefault(opposite));

            // DTO 생성 및 반환
            return ShowApplicationDto.builder()
                    .postId(post.getId())
                    .postTitle(post.getTitle())
                    .postImage(postImage.getPostImageUrl())
                    .oppositeNickname(opposite.getNickname())
                    .oppositeProfileImage(oppositeProfileImage.getMemberImage())
                    .applicationId(application.getId())
                    .applicationCreatedAt(application.getCreatedAt())
                    .applicationStatus(application.getStatus())
                    .build();
        });
    }

    public Page<ShowApplicationDto> showSentApplicationList(Long memberId, Integer page){
        // 1. 동행요청 목록 조회
        Page<Application> applicationPage = applicationQueryService.getSentApplicationList(memberId, page);

        // 2. Page.map()을 사용해 각 Application을 ShowApplicationDto로 변환
        return applicationPage.map(application -> {
            // 게시글 작성자 정보
            Post post = application.getPost();
            Member opposite = post.getMember();

            // 최신 이미지들 조회
            PostThumbnailResponseDto postImage = postImageQueryService.getLatestPostImage(post.getId());
            MemberProfileImage oppositeProfileImage = memberProfileImageQueryService
                    .getLatestPublicMemberProfileImage(opposite.getId()).orElse(MemberProfileImage.createDefault(opposite));

            // DTO 생성 및 반환
            return ShowApplicationDto.builder()
                    .postId(post.getId())
                    .postTitle(post.getTitle())
                    .postImage(postImage.getPostImageUrl())
                    .oppositeNickname(opposite.getNickname())
                    .oppositeProfileImage(oppositeProfileImage.getMemberImage())
                    .applicationId(application.getId())
                    .applicationCreatedAt(application.getCreatedAt())
                    .applicationStatus(application.getStatus())
                    .build();
        });
    }
}
