package umc.duckmelang.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PostImageRequestDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ImageMetadata {
        private Integer orderNumber;     // 이미지 순서
        private String description;      // 이미지 설명
    }

}
