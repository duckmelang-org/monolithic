package umc.duckmelang.domain.member.service.mypage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.auth.repository.AuthRepository;
import umc.duckmelang.domain.member.converter.MemberFilterConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.dto.MemberFilterDto;
import umc.duckmelang.domain.member.dto.MyPageRequestDto;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.redis.blacklist.BlacklistService;
import umc.duckmelang.global.security.jwt.JwtUtil;
import umc.duckmelang.global.security.user.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class MyPageCommandServiceImpl implements MyPageCommandService{
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BlacklistService blacklistService;

    @Override
    @Transactional
    public Member updateMemberProfile(Long memberId, MyPageRequestDto.UpdateMemberProfileDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        MemberFilterConverter.applyFilterRequest(member, request);
        return MemberFilterConverter.toFilterResponseDto(member);
    }

    @Override
    @Transactional
    public void deleteMember(HttpServletRequest request){
        CustomUserDetails userDetails = jwtUtil.extractUserDetailsFromSecurityContext();
        Long memberId = userDetails.getMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        if (member.getMemberStatus() == MemberStatus.DELETED) {
            throw new MemberException(ErrorStatus.ALREADY_DELETED_MEMBER);
        }

        member.deleteMember();
        memberRepository.save(member);

        String token = jwtUtil.extractToken(request);
        if (token != null && jwtUtil.validateToken(token)) {
            long expiration = jwtUtil.getExpirationFromToken(token);
            blacklistService.addToBlacklist(token, expiration);
        }
    }
}
