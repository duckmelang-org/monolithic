package umc.duckmelang.domain.idolcategory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.*;

public class IdolCategoryRequestDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class idolCategoryRequestList{
        List<idolCategoryRequestDto> dto;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class idolCategoryRequestDto {
        @NotNull(message = "이름 누락")
        private String name;

        @NotNull(message = "아이돌 id 누락")
        private Long idolId;
    }
}
