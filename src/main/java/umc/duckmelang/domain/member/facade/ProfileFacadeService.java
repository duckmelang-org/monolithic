package umc.duckmelang.domain.member.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.application.service.ApplicationQueryService;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.service.member.MemberQueryService;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.memberprofileimage.service.MemberProfileImageQueryService;
import umc.duckmelang.domain.post.service.PostQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.MemberProfileImageException;

@Service
@RequiredArgsConstructor
public class ProfileFacadeService {
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final ApplicationQueryService applicationService;
    private final MemberProfileImageQueryService memberProfileImageQueryService;

    @Transactional(readOnly = true)
    public MyPageResponseDto.MyPagePreviewDto getMyPageMemberPreview(Long memberId) {
        Member member = memberQueryService.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        // 대표 프로필 이미지 1개 조회
        MemberProfileImage latestPublicMemberProfileImage = memberProfileImageQueryService.getLatestPublicMemberProfileImage(memberId)
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));
        return MemberProfileConverter.toGetMemberPreviewResponseDto(member, latestPublicMemberProfileImage);
    }

    @Transactional(readOnly = true)
    public MyPageResponseDto.MyPageProfileDto getProfileByMemberId(Long memberId) {
        Member member = memberQueryService.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        // 포스트 수 조회
        int postCount = postQueryService.getPostCount(memberId);
        // 매칭 수 조회
        int matchCount = applicationService.countMatchedApplications(memberId);
        // 프로필 이미지 1개 조회
        MemberProfileImage memberProfileImage = memberProfileImageQueryService.getLatestPublicMemberProfileImage(memberId)
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));
        return MemberProfileConverter.toGetProfileResponseDto(member, memberProfileImage, postCount, matchCount);
    }
}