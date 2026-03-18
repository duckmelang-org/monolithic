package umc.duckmelang.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.post.domain.type.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class PostAddDto{
        private String title;
        private String content;
        private Integer maxParticipants;
        private PostStatus status;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostAddResultDto{
        private Long postId;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListDto{
        private List<PostListItemDto> postListItemDtoList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListItemDto{
        private Long postId;
        private String nickname;
        private String title;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailDto{
        private Long postId;
        private String title;
        private String content;
        private String nickname;
        private Integer maxParticipants;
        private Integer currentParticipants;
        private Long viewCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPopularListDto{
        private List<PostPopularItemDto> posts;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPopularItemDto{
        private Long postId;
        private String nickname;
        private String title;
        private Long viewCount;
        private LocalDateTime createdAt;
    }
}
