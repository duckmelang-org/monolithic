package umc.duckmelang.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.bookmark.repository.BookmarkRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.PostException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkQueryServiceImpl implements BookmarkQueryService {
    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    @Override
    public Page<Bookmark> getBookmarks(Long memberId, Integer page){
        return bookmarkRepository.findBookmarks(memberId, PageRequest.of(page, 10));
    }

//    스크랩 수 계산
    @Override
    public Integer getBookmarkCount(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));

        return post.getBookmarkList().size();

    }
}
