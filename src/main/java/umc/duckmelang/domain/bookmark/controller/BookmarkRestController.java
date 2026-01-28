package umc.duckmelang.domain.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.global.apipayload.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name="Bookmarks", description = "북마크 관련 API")
@Validated
public class BookmarkRestController {

    private final BookmarkQueryService bookmarkQueryService;
    private final BookmarkCommandService bookmarkCommandService;

    @GetMapping("/bookmarks")
    @CommonApiResponses
    @Operation(summary = "나의 동행 페이지 - 스크랩 내역 조회 API", description = "개인 스크랩 내역 조회 API입니다. responseBody 형태가 바뀌었으니 확인 부탁드립니다")
    public ApiResponse<BookmarkResponseDto.BookmarkPreviewListDto> getBookmarks(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(name = "page",  defaultValue = "0") Integer page) {
        Page<Bookmark> postList = bookmarkQueryService.getBookmarks(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(BookmarkConverter.bookmarkPreviewListDto(postList));
    }

    @PostMapping("/posts/{postId}/bookmarks")
    @CommonApiResponses
    @Operation(summary = "게시글 스크랩 API", description = "게시글 스크랩하는 API 입니다.")
    public ApiResponse<BookmarkResponseDto.BookmarkJoinResultDto>joinBookmark (@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name="postId") Long postId){
        Bookmark bookmark = bookmarkCommandService.joinBookmark(postId, userDetails.getMemberId());
        return ApiResponse.onSuccess(BookmarkConverter.bookmarkJoinResultDto(bookmark));
    }

    @DeleteMapping("/posts/{postId}/bookmarks")
    @CommonApiResponses
    @Operation(summary="게시글 스크랩 삭제 API", description = "스크랩 취소하는 API입니다.")
    public ApiResponse<String> deleteBookmark (@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name="postId") Long postId) {
        bookmarkCommandService.deleteBookmark(postId, userDetails.getMemberId());
        return ApiResponse.onSuccess("스크랩을 성공적으로 삭제했습니다");
    }
}
