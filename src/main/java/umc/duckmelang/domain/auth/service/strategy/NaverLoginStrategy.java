package umc.duckmelang.domain.auth.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.auth.client.NaverApiClient;
import umc.duckmelang.domain.auth.dto.naver.NaverUserInfoResponse;
import umc.duckmelang.domain.auth.dto.response.LoginResponse;
import umc.duckmelang.domain.auth.jwt.JwtTokenProvider;
import umc.duckmelang.domain.auth.service.AuthService;
import umc.duckmelang.domain.auth.service.SocialService;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.LoginType;

@Component("NAVER")
@RequiredArgsConstructor
public class NaverLoginStrategy implements SocialLoginStrategy{
    private final NaverApiClient naverApiClient;
    private final JwtTokenProvider  jwtTokenProvider;
    private final SocialService socialService;

    @Override
    @Transactional
    public LoginResponse login(String code){
        String accessToken = naverApiClient.getAccessToken(code);
        NaverUserInfoResponse userInfo = naverApiClient.getUserInfo(accessToken);

        String email = userInfo.response().email();
        String naverId = userInfo.response().id();

        Member member = socialService.findOrCreate(email, naverId, LoginType.NAVER);

        String accessTokenJwt = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole().name());
        String refreshTokenJwt = jwtTokenProvider.generateRefreshToken(member.getId(), member.getRole().name());

        return new LoginResponse(member.getId(), accessTokenJwt, refreshTokenJwt, member.isProfileComplete());
    }
}
