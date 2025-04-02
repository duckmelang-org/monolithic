package umc.duckmelang.domain.bookmark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.post.domain.Post;

public interface BookmarkQueryService {
    Page<Bookmark> getBookmarks(Long memberId, Integer page);
    Integer getBookmarkCount(Long postId);
}
