package umc.duckmelang.domain.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.bookmark.converter.BookmarkConverter;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.bookmark.dto.BookmarkResponseDto;
import umc.duckmelang.domain.bookmark.service.BookmarkCommandService;
import umc.duckmelang.domain.bookmark.service.BookmarkQueryService;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.global.security.user.CustomUserDetails;
import umc.duckmelang.global.validation.annotation.ValidPageNumber;
import umc.duckmelang.global.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@Validated
public class BookmarkRestController {

    private final BookmarkQueryService bookmarkQueryService;
    private final BookmarkCommandService bookmarkCommandService;

    @GetMapping("/bookmarks")
    @CommonApiResponses
    @Operation(summary = "나의 동행 페이지 - 스크랩 내역 조회 API", description = "개인 스크랩 내역 조회 API입니다. responseBody 형태가 바뀌었으니 확인 부탁드립니다")
    public ApiResponse<BookmarkResponseDto.BookmarkPreviewListDto> getBookmarks(@AuthenticationPrincipal CustomUserDetails userDetails, @ValidPageNumber @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<Bookmark> postList = bookmarkQueryService.getBookmarks(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(BookmarkConverter.bookmarkPreviewListDto(postList));
    }

    @PostMapping("posts/{postId}/bookmarks")
    @CommonApiResponses
    @Operation(summary = "게시글 스크랩 API", description = "게시글 스크랩하는 API 입니다.")
    public ApiResponse<BookmarkResponseDto.BookmarkJoinResultDto>joinBookmark (@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name="postId") Long postId){
        Bookmark bookmark = bookmarkCommandService.joinBookmark(postId, userDetails.getMemberId());
        return ApiResponse.onSuccess(BookmarkConverter.bookmarkJoinResultDto(bookmark));
    }

    @DeleteMapping("bookmarks/{bookmarkId}")
    @CommonApiResponses
    @Operation(summary="게시글 스크랩 삭제 API", description = "스크랩 삭제하는 API 입니다. bookmarkId 조회는 나의 동행 페이지- 스크랩 내역 조회 API 에 결과로 나오도록 추가해두었습니다.")
    public ApiResponse<String> deleteBookmark (@PathVariable(name="bookmarkId") Long bookmarkId) {
        bookmarkCommandService.deleteBookmark(bookmarkId);
        return ApiResponse.onSuccess("스크랩을 성공적으로 삭제했습니다");
    }
}
