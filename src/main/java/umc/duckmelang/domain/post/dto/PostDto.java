package umc.duckmelang.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class PostAddDto{
        private String title;
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostAddResultDto{
        private Long postId;
        private LocalDateTime createdAt;
    }
}
