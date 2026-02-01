package umc.duckmelang.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.auth.user.CustomUserDetails;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostDto;
import umc.duckmelang.domain.post.service.PostCommandService;
import umc.duckmelang.global.apipayload.ApiResponse;

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "Post", description = "게시글 API")
@RequiredArgsConstructor
public class PostController {

    private final PostCommandService postCommandService;

    @Operation(summary = "게시글 작성 API", description = "게시글을 작성하는 API입니다.")
    @PostMapping("/add")
    public ApiResponse<PostDto.PostAddResultDto> addPost(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestBody @Valid PostDto.PostAddDto request){
        Post post = postCommandService.addPost(request, userDetails.getMemberId());
        return ApiResponse.onSuccess(PostConverter.toPostAddResultDto(post));
    }
}
