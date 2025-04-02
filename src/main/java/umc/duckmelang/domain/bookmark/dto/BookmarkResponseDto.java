package umc.duckmelang.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.post.dto.PostResponseDto;

import java.util.List;

public class BookmarkResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkJoinResultDto{
        private Long bookmarkId;
        private Long memberId;
        private Long postId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkPreviewDto {
        private Long bookmarkId;
        private PostResponseDto.PostPreviewDto post;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkPreviewListDto {
        private List<BookmarkPreviewDto> bookmarkList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }
}
