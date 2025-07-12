package umc.duckmelang.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.member.domain.enums.Role;

public class AdminResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminManagerDto{
        private Long memberId;
        private String loginId;
        private Role role;
    }
}
