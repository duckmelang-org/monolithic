package umc.duckmelang.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostDto;
import umc.duckmelang.domain.post.service.PostCommandService;
import umc.duckmelang.domain.post.service.PostQueryService;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "Post", description = "게시글 API")
@RequiredArgsConstructor
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Operation(summary = "게시글 작성 API", description = "게시글을 작성하는 API입니다.")
    @PostMapping("/add")
    public ApiResponse<PostDto.PostAddResultDto> addPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestBody @Valid PostDto.PostAddDto request){
        Post post = postCommandService.addPost(request, userDetails.getMemberId());
        return ApiResponse.onSuccess(PostConverter.toPostAddResultDto(post));
    }

    @Operation(summary = "게시글 조회 API", description = "전체 게시글을 조회하는 API입니다.")
    @GetMapping("/list")
    public ApiResponse<PostDto.PostListDto> postList(@RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "10") int size){
        Page<Post> postList = postQueryService.getPostList(page, size);
        return ApiResponse.onSuccess(PostConverter.toPostListDto(postList));
    }

    @Operation(summary = "게시글 단 건 조회 API", description = "게시글 하나를 조회하는 API입니다.")
    @GetMapping("/{postId}")
    public ApiResponse<PostDto.PostDetailDto> getPostItem(@PathVariable(name = "postId") Long postId){
        Post post = postQueryService.getPost(postId);
        return ApiResponse.onSuccess(PostConverter.toPostDetailDto(post));
    }
}
