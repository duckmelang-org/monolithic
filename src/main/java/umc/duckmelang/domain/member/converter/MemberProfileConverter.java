package umc.duckmelang.domain.member.converter;

import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.mypage.MyPageResponseDto;
import umc.duckmelang.domain.member.domain.MemberProfileImage;

@Component
public class MemberProfileConverter {

    public static MyPageResponseDto.MyPagePreviewDto toGetMemberPreviewResponseDto(Member member, MemberProfileImage memberProfileImage) {
        return MyPageResponseDto.MyPagePreviewDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .age(member.calculateAge())
                .latestPublicMemberProfileImage(memberProfileImage.getMemberImage())
                .build();
    }

    public static MyPageResponseDto.MyPageProfileDto toGetProfileResponseDto(Member member, MemberProfileImage memberProfileImage, long postCount, long matchCount) {
        return  MyPageResponseDto.MyPageProfileDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .gender(member.getGender())
                .age(member.calculateAge())
                .latestPublicMemberProfileImage(memberProfileImage.getMemberImage())
                .introduction(member.getIntroduction())
                .postCount(postCount)
                .matchCount(matchCount)
                .build();
    }

    public static MyPageResponseDto.MyPageProfileEditBeforeDto toMemberProfileEditBeforeDto(Member member, MemberProfileImage profileImage) {
        return MyPageResponseDto.MyPageProfileEditBeforeDto.builder()
                .nickname(member.getNickname())
                .latestPublicMemberProfileImage(profileImage != null ? profileImage.getMemberImage() : null)
                .build();
    }

    public static MyPageResponseDto.MyPageProfileEditAfterDto toMemberProfileEditAfterDto(Member updatedMember) {
        return MyPageResponseDto.MyPageProfileEditAfterDto.builder()
                .memberId(updatedMember.getId())
                .nickname(updatedMember.getNickname())
                .introduction(updatedMember.getIntroduction())
                .build();
    }
}
