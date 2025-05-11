package umc.duckmelang.domain.member.service.member;

import umc.duckmelang.domain.landmine.domain.Landmine;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.member.MemberRequestDto;
import umc.duckmelang.domain.member.dto.member.MemberSignUpDto;
import umc.duckmelang.domain.member.domain.MemberEvent;
import umc.duckmelang.domain.member.domain.MemberIdol;

import java.util.List;

public interface MemberCommandService {
    Member signupMember(MemberSignUpDto.SignupDto request);
    Member registerProfile(Long memberId, MemberRequestDto.ProfileRequestDto request);
    List<MemberIdol> selectIdols(Long memberId, MemberRequestDto.SelectIdolsDto request);
    List<MemberEvent> selectEvents(Long memberId, MemberRequestDto.SelectEventsDto request);
    List<Landmine> createLandmines(Long memberId, MemberRequestDto.CreateLandminesDto request);
    Member createIntroduction(Long memberId, MemberRequestDto.CreateIntroductionDto request);
    boolean isNicknameExists(String nickname);
}
