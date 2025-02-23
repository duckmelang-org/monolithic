package umc.duckmelang.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostPreviewDto{
        private Long postId;
        private String title;
        private String category;
        private LocalDate date;
        private String nickname;
        private LocalDateTime createdAt;
        private String postImageUrl;
        private String latestPublicMemberProfileImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostPreviewListDto{
        List<PostPreviewDto> postList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostDetailDto{
        private Long memberId;
        private String nickname;
        private Integer age;
        private Gender gender;
        private Double averageScore;
        private Integer bookmarkCount;
        private Integer viewCount;
        private Integer chatCount;
        private String title;
        private String content;
        private Short wanted;
        private List<String> idol;
        private String category;
        private LocalDate date;
        private LocalDateTime createdAt;
        private List<String> postImageUrl;
        private String latestPublicMemberProfileImage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostJoinResultDto{
        private Long id;
        private String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostStatusDto{
        private Long id;
        private String title;
        private Short wanted;
    }
}
