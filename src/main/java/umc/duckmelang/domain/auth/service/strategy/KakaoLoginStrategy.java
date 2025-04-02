package umc.duckmelang.domain.auth.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.auth.client.KakaoApiClient;
import umc.duckmelang.domain.auth.dto.kakao.KakaoUserInfoResponse;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.auth.service.AuthService;
import umc.duckmelang.domain.auth.service.SocialService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.LoginType;
import umc.duckmelang.domain.member.domain.enums.MemberStatus;
import umc.duckmelang.domain.member.domain.enums.Role;
import umc.duckmelang.domain.member.repository.MemberRepository;

@Component("KAKAO")
@RequiredArgsConstructor
public class KakaoLoginStrategy implements SocialLoginStrategy {
    private final KakaoApiClient kakaoApiClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialService socialService;

    @Override
    @Transactional
    public LoginResponse login(String code){
        String kakaoAccessToken = kakaoApiClient.getAccessToken(code);
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(kakaoAccessToken);
        String email = userInfo.kakaoAccount().email();

        // 공통 가입 로직 호출
        Member member = socialService.findOrCreate(email, String.valueOf(userInfo.id()), LoginType.KAKAO);

        // JWT 발급
        String accessTokenJwt = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole().name());
        String refreshTokenJwt = jwtTokenProvider.generateRefreshToken(member.getId(), member.getRole().name());

        return new LoginResponse(member.getId(), accessTokenJwt, refreshTokenJwt, member.isProfileComplete()
        );
    }
}
