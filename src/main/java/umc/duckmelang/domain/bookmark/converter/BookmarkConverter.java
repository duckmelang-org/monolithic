package umc.duckmelang.domain.bookmark.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.bookmark.dto.BookmarkResponseDto;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookmarkConverter {
    public static BookmarkResponseDto.BookmarkJoinResultDto bookmarkJoinResultDto(Bookmark bookmark) {
        return BookmarkResponseDto.BookmarkJoinResultDto.builder()
                .bookmarkId(bookmark.getId())
                .memberId(bookmark.getMember().getId())
                .postId(bookmark.getPost().getId())
                .build();
    }

    public static Bookmark toBookmark(Member member, Post post) {
        return Bookmark.builder()
                .member(member)
                .post(post)
                .build();

    }

    public static BookmarkResponseDto.BookmarkPreviewDto bookmarkPreviewDto(Bookmark bookmark) {
        return BookmarkResponseDto.BookmarkPreviewDto.builder()
                .bookmarkId(bookmark.getId())
                .post(PostConverter.postPreviewDto(bookmark.getPost()))
                .build();
    }

    public static BookmarkResponseDto.BookmarkPreviewListDto bookmarkPreviewListDto(Page<Bookmark> bookmarkPage) {
        List<BookmarkResponseDto.BookmarkPreviewDto> dtoList = bookmarkPage.stream()
                .map(BookmarkConverter::bookmarkPreviewDto)
                .collect(Collectors.toList());

        return BookmarkResponseDto.BookmarkPreviewListDto.builder()
                .bookmarkList(dtoList)
                .listSize(dtoList.size())
                .totalPage(bookmarkPage.getTotalPages())
                .totalElements(bookmarkPage.getTotalElements())
                .isFirst(bookmarkPage.isFirst())
                .isLast(bookmarkPage.isLast())
                .currentPage(bookmarkPage.getNumber())
                .build();
    }

}