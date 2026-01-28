package umc.duckmelang.domain.bookmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.duckmelang.domain.bookmark.converter.BookmarkConverter;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.bookmark.repository.BookmarkRepository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.repository.MemberRepository;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.repository.PostRepository;
import umc.duckmelang.domain.post.dto.PostThumbnailResponseDto;
import umc.duckmelang.domain.post.service.postImage.PostImageQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.*;
import umc.duckmelang.global.apipayload.exception.MemberException;
import umc.duckmelang.global.apipayload.exception.PostException;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkCommandServiceImpl implements BookmarkCommandService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostImageQueryService postImageQueryService;

    @Override
    public Bookmark joinBookmark(Long postId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Post post = getPostOrThrow(postId);
        validateSelfBookmark(member, post);
        validateDuplicateBookmark(member, post);

        // 게시물 대표 이미지 URL 조회
        PostThumbnailResponseDto postThumbnail = postImageQueryService.getLatestPostImage(postId);
        String postImageUrl = (postThumbnail != null) ? postThumbnail.getPostImageUrl() : null;

        Bookmark bookmark = BookmarkConverter.toBookmark(member, post);

        return bookmarkRepository.save(bookmark);
    }

    @Override
    public void deleteBookmark(Long postId, Long memberId) {
        Member member = getMemberOrThrow(memberId);
        Post post = getPostOrThrow(postId);
        Bookmark bookmark = getBookmarkOrThrow(member, post);
        bookmarkRepository.delete(bookmark);
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorStatus.POST_NOT_FOUND));
    }

    private Bookmark getBookmarkOrThrow(Member member, Post post) {
        return bookmarkRepository.findByMemberAndPost(member, post)
                .orElseThrow(() -> new BookmarkException(ErrorStatus.INVALID_BOOKMARK));
    }

    private void validateDuplicateBookmark(Member member, Post post) {
        if (bookmarkRepository.existsByMemberAndPost(member, post)) {
            throw new BookmarkException(ErrorStatus.DUPLICATE_BOOKMARK);
        }
    }

    private void validateSelfBookmark(Member member, Post post) {
        if (member.getId().equals(post.getMember().getId())) {
            throw new BookmarkException(ErrorStatus.CANNOT_BOOKMARK_OWN_POST);
        }
    }
}


