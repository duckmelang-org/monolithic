package umc.duckmelang.domain.auth.dto.kakao;

/**
 * 카카오 사용자 정보 응답 객체
 * 카카오 OAuth API에서 사용자 정보를 요청했을 때 응답받는 최상위 JSON 구조를 매핑한 DTO
 * - id : 카카오 내부 사용자 고유 ID
 * - kakao_account : 이메일 정보 등을 포함한 계정 정보 객체
 */
public record KakaoUserInfoResponse (Long id, KakaoAccount kakaoAccount
){}

