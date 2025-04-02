package umc.duckmelang.domain.auth.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.auth.client.KakaoApiClient;
import umc.duckmelang.domain.auth.dto.kakao.KakaoUserInfoResponse;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.LoginType;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;

@Component("KAKAO")
@RequiredArgsConstructor
public class KakaoLoginStrategy implements SocialLoginStrategy {
    private final KakaoApiClient kakaoApiClient;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponse login(String accessToken){
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(accessToken);
        String email = userInfo.kakaoAccount().email();

        // 사용자 존재 여부 확인
        Member member = memberRepository.findByEmail(email)
                .orElseGet(()-> registerNewSocialMember(email));

        // JWT 발급
        String accessTokenJwt = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole().name());
        String refreshTokenJwt = jwtTokenProvider.generateRefreshToken(member.getId(), member.getRole().name());

        return new LoginResponse(member.getId(), accessTokenJwt, refreshTokenJwt, member.isProfileComplete()
        );
    }

    // 소셜 로그인 사용자가 db 에 없을 경우 추가
    private Member registerNewSocialMember(String email) {
        Member newMember = Member.builder()
                .email(email)
                .role(Role.USER)
                .loginType(LoginType.KAKAO)
                .memberStatus(MemberStatus.ACTIVE)
                .build();
        return memberRepository.save(newMember);
    }
}
