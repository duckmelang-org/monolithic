package umc.duckmelang.domain.post.domain.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PostStatus {
    RECRUITING("모집 중"),
    CLOSED("모집 완료"),
    CANCELED("게시글 취소");

    private final String label;

    public String getLabel() {
        return label;
    }
}
