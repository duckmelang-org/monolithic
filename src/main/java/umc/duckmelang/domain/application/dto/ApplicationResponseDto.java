package umc.duckmelang.domain.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.*;
import umc.duckmelang.domain.application.domain.enums.ApplicationStatus;

import java.time.LocalDateTime;

public class ApplicationResponseDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    // 실패, 취소로 상태 변환 시 사용
    public static class CommonApplicationResponseDto {
        ApplicationStatus newStatus;
        LocalDateTime modifiedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    // 성공 상태 변환 시 mateRelationship 생겼음을 알림
    public static class MateRelationshipCreateResponseDto {
        Long mateRelationshipId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShowApplicationListDto {
        private List<ShowApplicationDto> applicationList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
        private Integer currentPage;
    }
}
