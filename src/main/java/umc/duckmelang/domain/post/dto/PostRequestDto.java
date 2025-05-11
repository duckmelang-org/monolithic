package umc.duckmelang.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class PostRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostJoinDto{
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        @NotNull
        private List<Long> idolIds;
        @NotNull
        private Long categoryId;
        @NotNull
        private LocalDate date;
        @Size(max = 5)
        private List<PostImageRequestDto.ImageMetadata> imageInfos;  // 이미지 메타데이터 리스트
    }
}
