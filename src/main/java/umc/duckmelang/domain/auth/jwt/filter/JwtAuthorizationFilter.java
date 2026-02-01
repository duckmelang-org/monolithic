package umc.duckmelang.domain.auth.jwt.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.duckmelang.domain.auth.jwt.JwtUtil;

import java.io.IOException;

/**
 * JWT 인증 필터
 *  요청 헤더에 포함된 JWT 토큰을 검증하고 인증 정보를 설정
 */
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = jwtUtil.extractToken(request);

        if(token!=null){
            try{
                jwtUtil.validateToken(token);
                Authentication authentication = jwtUtil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch(JwtException e){
                throw new AuthenticationCredentialsNotFoundException(e.getMessage(), e);
            }
        }
        chain.doFilter(request, response);
    }
}
