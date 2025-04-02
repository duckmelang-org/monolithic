package umc.duckmelang.domain.bookmark.service;

import umc.duckmelang.domain.bookmark.domain.Bookmark;

public interface BookmarkCommandService {
    Bookmark joinBookmark(Long postId, Long memberId);
    void deleteBookmark(Long bookmarkId);

}
