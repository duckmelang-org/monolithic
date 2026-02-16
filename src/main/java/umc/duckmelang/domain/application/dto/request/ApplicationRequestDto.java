package umc.duckmelang.domain.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApplicationRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequestDto{
        private Long postId;
    }
}
