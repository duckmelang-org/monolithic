package umc.duckmelang.domain.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.member.converter.MemberConverter;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.dto.MemberSignUpDto;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.GeneralException;
import umc.duckmelang.global.apipayload.exception.MemberException;

@Service
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Member signupMember(MemberSignUpDto.SignupDto request){
        if(memberRepository.existsByLoginId(request.getLoginId())){
            throw new MemberException(ErrorStatus.DUPLICATE_LOGINID);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member newMember = MemberConverter.toMember(request, encodedPassword);

        return memberRepository.save(newMember);
    }

    @Override
    @Transactional
    public void updateFcmToken(Long memberId, String fcmToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
        member.updateFcmToken(fcmToken);
    }
}
