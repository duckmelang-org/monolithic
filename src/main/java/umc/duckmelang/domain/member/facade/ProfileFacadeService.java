package umc.duckmelang.domain.member.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.application.service.ApplicationQueryService;
import umc.duckmelang.domain.member.converter.MemberProfileConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.service.member.MemberQueryService;
import umc.duckmelang.domain.member.domain.MemberProfileImage;
import umc.duckmelang.domain.member.service.profileImage.MemberProfileImageQueryService;
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
        Member member = getMemberOrThrow(memberId);
        MemberProfileImage profileImage = getLatestPublicProfileImageOrThrow(memberId);

        return MemberProfileConverter.toGetMemberPreviewResponseDto(member, profileImage);
    }

    @Transactional(readOnly = true)
    public MyPageResponseDto.MyPageProfileDto getProfileByMemberId(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        MemberProfileImage profileImage = getLatestPublicProfileImageOrThrow(memberId);

        // 포스트 수 조회
        int postCount = postQueryService.getPostCount(memberId);
        // 매칭 수 조회
        int matchCount = applicationService.countMatchedApplications(memberId);

        return MemberProfileConverter.toGetProfileResponseDto(member, profileImage, postCount, matchCount);
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberQueryService.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private MemberProfileImage getLatestPublicProfileImageOrThrow(Long memberId) {
        return memberProfileImageQueryService.getLatestPublicMemberProfileImage(memberId)
                .orElseThrow(() -> new MemberProfileImageException(ErrorStatus.MEMBER_PROFILE_IMAGE_NOT_FOUND));
    }
}