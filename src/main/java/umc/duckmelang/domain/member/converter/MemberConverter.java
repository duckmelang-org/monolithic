package umc.duckmelang.domain.member.converter;

import umc.duckmelang.domain.eventcategory.domain.EventCategory;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.landmine.domain.Landmine;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.dto.MemberResponseDto;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;
import umc.duckmelang.domain.memberevent.domain.MemberEvent;
import umc.duckmelang.domain.memberidol.domain.MemberIdol;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;

import java.util.List;
import java.util.stream.Collectors;

import java.util.ArrayList;

@Component
public class MemberConverter {

    public static Member toMember(MemberSignUpDto.SignupDto request){
        return Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .memberStatus(MemberStatus.ACTIVE)
                .isProfileComplete(false)
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

    public static MemberResponseDto.SelectIdolsResultDto toSelectIdolResponseDto(List<MemberIdol> memberIdolList) {

        Member member = memberIdolList.get(0).getMember(); // 반환된 리스트 내 모든 MemberIdol은 같은 Member를 참조하고 있음을 전제
        List<Long> idolCategoryIds = memberIdolList.stream()
                .map(memberIdol -> memberIdol.getIdolCategory().getId())
                .toList();

        return MemberResponseDto.SelectIdolsResultDto.builder()
                .memberId(member.getId())
                .idolCategoryIds(idolCategoryIds)
                .build();
    }

    public static MemberEvent toMemberEvent(Member member, EventCategory eventCategory) {
        return MemberEvent.builder()
                .member(member)
                .eventCategory(eventCategory)
                .build();
    }

    public static MemberResponseDto.SelectEventsResultDto toSelectEventResponseDto(List<MemberEvent> memberEventList) {
        // memberEventList가 비어있을 경우
        if (memberEventList == null || memberEventList.isEmpty()) {
            return MemberResponseDto.SelectEventsResultDto.builder()
                    .memberId(null) // memberId를 null로 설정
                    .eventCategoryIds(new ArrayList<>()) // 빈 리스트 반환
                    .build();
        }

        Member member = memberEventList.get(0).getMember(); // 반환된 리스트 내 모든 MemberEvent는 같은 Member를 참조하고 있음을 전제
        List<Long> eventCategoryIds = memberEventList.stream()
                .map(memberEvent -> memberEvent.getEventCategory().getId())
                .toList();

        return MemberResponseDto.SelectEventsResultDto.builder()
                .memberId(member.getId())
                .eventCategoryIds(eventCategoryIds)
                .build();

    }

    public static Landmine toLandmine(Member member, String content) {
        return Landmine.builder()
                .member(member)
                .content(content)
                .build();
    }

    public static MemberResponseDto.CreateLandmineResultDto toCreateLandmineResponseDto(List<Landmine> landmineList) {
        // landmineList가 비어있을 경우
        if (landmineList == null || landmineList.isEmpty()) {
            return MemberResponseDto.CreateLandmineResultDto.builder()
                    .memberId(null) // memberId를 null로 설정
                    .landmineContents(new ArrayList<>()) // 빈 리스트 반환
                    .build();
        }

        Member member = landmineList.get(0).getMember(); // 반환된 리스트 내 모든 MemberEvent는 같은 Member를 참조하고 있음을 전제
        List<String> landmineContents = landmineList.stream()
                .map(Landmine::getContent)
                .collect(Collectors.toList());

        return MemberResponseDto.CreateLandmineResultDto.builder()
                .memberId(member.getId())
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

    public static Member toMemberWithIntroduction(Member member, String introduction) {
        member.updateIntroduction(introduction);
        return member;
    }

    public static MemberResponseDto.CreateIntroductionResultDto toCreateIntroductionResponseDto(Member member) {
        return MemberResponseDto.CreateIntroductionResultDto.builder()
                .memberId(member.getId())
                .introduction(member.getIntroduction())
                .build();
    }
}
