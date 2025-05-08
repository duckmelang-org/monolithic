package umc.duckmelang.domain.auth.dto.kakao;

/**
 * 카카오 사용자 계정 정보 응답 객체
 * 카카오에서 사용자 계정 정보를 응답할 때 포함되는 객체 중 'kakao_account' 부분을 매핑
 * 일단은 이메일만을 포함하지만, 필요 시 다른 필드를 추가할 수 있음 !
 */
public record KakaoAccount(String email){
}
