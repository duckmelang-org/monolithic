package umc.duckmelang.domain.member.service.mypage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.converter.MemberFilterConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.dto.member.MemberFilterDto;
import umc.duckmelang.domain.member.dto.mypage.MyPageRequestDto;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;

@Service
@RequiredArgsConstructor
public class MyPageCommandServiceImpl implements MyPageCommandService{
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member updateMemberProfile(Long memberId, MyPageRequestDto.UpdateMemberProfileDto request) {
        Member member = findMemberOrThrow(memberId);
        if(!member.getNickname().equals(request.getNickname())){
            if(memberRepository.existsByNickname(request.getNickname())){
                throw new MemberException(ErrorStatus.DUPLICATE_NICKNAME);
            }
        }
        member.updateProfile(request.getNickname(), request.getIntroduction());
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public MemberFilterDto.FilterResponseDto setFilter(Long memberId, MemberFilterDto.FilterRequestDto request){
        Member member = findMemberOrThrow(memberId);
        MemberFilterConverter.applyFilterRequest(member, request);
        return MemberFilterConverter.toFilterResponseDto(member);
    }

    private Member findMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }
}
