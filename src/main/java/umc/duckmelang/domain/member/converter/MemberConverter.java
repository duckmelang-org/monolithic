package umc.duckmelang.domain.member.converter;

import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.landmine.domain.Landmine;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.dto.member.MemberResponseDto;
import umc.duckmelang.domain.member.dto.member.MemberSignUpDto;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.domain.MemberEvent;
import umc.duckmelang.domain.member.domain.MemberIdol;
import umc.duckmelang.domain.member.domain.MemberProfileImage;

import java.util.List;

@Component
public class MemberConverter {

    public static Member toMember(MemberSignUpDto.SignupDto request, String encodedPassword) {
        return Member.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .memberStatus(MemberStatus.ACTIVE)
                .isProfileComplete(false)
                .role(Role.USER)
                .build();
    }

    public static MemberSignUpDto.SignupResultDto toSignupResultDto(Member member){
        return MemberSignUpDto.SignupResultDto.builder()
                .memberId(member.getId())
                .createdAt(member.getCreatedAt())
                .profileComplete(member.isProfileComplete())
                .build();
    }

    public static MemberResponseDto.ProfileResultDto toProfileResponseDto(Member member){
        return MemberResponseDto.ProfileResultDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .birth(member.getBirth())
                .gender(member.getGender())
                .build();
    }

    public static MemberIdol toMemberIdol(Member member, IdolCategory idolCategory) {
        return MemberIdol.builder()
                .member(member)
                .idolCategory(idolCategory)
                .build();
    }

    public static MemberResponseDto.SelectIdolsResultDto toSelectIdolResponseDto(Long memberId, List<MemberIdol> memberIdolList) {
        List<Long> idolCategoryIds = memberIdolList.stream()
                .map(memberIdol -> memberIdol.getIdolCategory().getId())
                .toList();

        return MemberResponseDto.SelectIdolsResultDto.builder()
                .memberId(memberId)
                .idolCategoryIds(idolCategoryIds)
                .build();
    }

    public static MemberEvent toMemberEvent(Member member, EventCategory eventCategory) {
        return MemberEvent.builder()
                .member(member)
                .eventCategory(eventCategory)
                .build();
    }

    public static MemberResponseDto.SelectEventsResultDto toSelectEventResponseDto(Long memberId, List<MemberEvent> memberEventList) {
        List<Long> eventCategoryIds = memberEventList.stream()
                .map(memberEvent -> memberEvent.getEventCategory().getId())
                .toList();

        return MemberResponseDto.SelectEventsResultDto.builder()
                .memberId(memberId)
                .eventCategoryIds(eventCategoryIds)
                .build();
    }

    public static Landmine toLandmine(Member member, String content) {
        return Landmine.builder()
                .member(member)
                .content(content)
                .build();
    }

    public static MemberResponseDto.CreateLandmineResultDto toCreateLandmineResponseDto(Long memberId, List<Landmine> landmineList) {
        List<String> landmineContents = landmineList.stream()
                .map(Landmine::getContent)
                .toList();

        return MemberResponseDto.CreateLandmineResultDto.builder()
                .memberId(memberId)
                .landmineContents(landmineContents)
                .build();
    }

    public static MemberResponseDto.CreateMemberProfileImageResultDto toCreateMemberProfileImageResponseDto(MemberProfileImage memberProfileImage) {
        return MemberResponseDto.CreateMemberProfileImageResultDto.builder()
                .memberId(memberProfileImage.getMember().getId())
                .memberProfileImageURL(memberProfileImage.getMemberImage())
                .isPublic(memberProfileImage.isPublic())
                .build();
    }
}
