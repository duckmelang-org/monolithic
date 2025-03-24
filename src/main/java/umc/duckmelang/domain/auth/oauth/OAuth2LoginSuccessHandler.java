package umc.duckmelang.domain.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.auth.dto.AuthResponseDto;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.global.apipayload.ApiResponse;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.security.redis.RefreshTokenService;
import umc.duckmelang.global.security.jwt.JwtTokenProvider;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        final String email;
        final String provider;

        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            provider = "KAKAO";
        } else if (attributes.containsKey("email")) {
            email = (String) attributes.get("email");
            provider = "GOOGLE";
        } else {
            throw new IllegalStateException("지원하지 않는 OAuth2 Provider입니다.");
        }

        if (email == null) {
            ErrorStatus errorStatus = ErrorStatus.DUPLICATE_EMAIL;
            response.sendError(errorStatus.getHttpStatus().value(), errorStatus.getMessage());
            return;
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(ErrorStatus.AUTH_USER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId(), member.getRole().name());
        refreshTokenService.saveRefreshToken(refreshToken, member.getId());

        AuthResponseDto.LoginResponse loginResponse = AuthResponseDto.LoginResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .provider(provider)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .profileComplete(member.isProfileComplete())
                .build();

        ApiResponse<AuthResponseDto.LoginResponse> apiResponse = ApiResponse.onSuccess(loginResponse);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }
}

