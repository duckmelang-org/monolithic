package umc.duckmelang.domain.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ApplicationRequestDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequestDto{
        private Long postId;
    }
}
