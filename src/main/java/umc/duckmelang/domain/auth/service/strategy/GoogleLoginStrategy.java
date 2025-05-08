package umc.duckmelang.domain.auth.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.auth.client.GoogleApiClient;
import umc.duckmelang.domain.auth.dto.google.GoogleUserInfoResponse;
import umc.duckmelang.domain.auth.dto.kakao.KakaoUserInfoResponse;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.auth.service.SocialService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.LoginType;

@Component("GOOGLE")
@RequiredArgsConstructor
public class GoogleLoginStrategy implements SocialLoginStrategy{
    private final GoogleApiClient googleApiClient;
    private final SocialService socialService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponse login(String code){
        String googleAccessToken = googleApiClient.getAccessToken(code);
        GoogleUserInfoResponse userInfo = googleApiClient.getUserInfo(googleAccessToken);
        String email = userInfo.email();

        // 공통 가입 로직 호출
        Member member = socialService.findOrCreate(email, userInfo.id(), LoginType.GOOGLE);

        // JWT 발급
        String accessTokenJwt = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole().name());
        String refreshTokenJwt = jwtTokenProvider.generateRefreshToken(member.getId(), member.getRole().name());

        return new LoginResponse(member.getId(), accessTokenJwt, refreshTokenJwt, member.isProfileComplete()
        );
    }
}
