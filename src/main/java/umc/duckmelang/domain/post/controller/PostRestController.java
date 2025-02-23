package umc.duckmelang.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.eventcategory.dto.EventCategoryResponseDto;
import umc.duckmelang.domain.eventcategory.service.EventCategoryQueryService;
import umc.duckmelang.domain.idolcategory.converter.IdolCategoryConverter;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;
import umc.duckmelang.domain.idolcategory.dto.IdolCategoryResponseDto;
import umc.duckmelang.domain.idolcategory.service.IdolCategoryQueryService;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.member.dto.MemberFilterDto;
import umc.duckmelang.domain.member.service.mypage.MyPageQueryService;
import umc.duckmelang.domain.memberidol.converter.MemberIdolConverter;
import umc.duckmelang.domain.memberidol.domain.MemberIdol;
import umc.duckmelang.domain.memberidol.dto.MemberIdolResponseDto;
import umc.duckmelang.domain.memberidol.service.MemberIdolCommandService;
import umc.duckmelang.domain.memberidol.service.MemberIdolQueryService;
import umc.duckmelang.global.security.user.CustomUserDetails;
import umc.duckmelang.global.validation.annotation.ExistIdol;
import umc.duckmelang.domain.post.converter.PostConverter;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostRequestDto;
import umc.duckmelang.domain.post.dto.PostResponseDto;
import umc.duckmelang.domain.post.facade.PostFacadeService;
import umc.duckmelang.domain.post.service.PostCommandService;
import umc.duckmelang.domain.post.service.PostQueryService;
import umc.duckmelang.global.validation.annotation.ExistPost;
import umc.duckmelang.global.validation.annotation.ValidPageNumber;
import umc.duckmelang.global.annotations.CommonApiResponses;
import umc.duckmelang.global.apipayload.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
public class PostRestController {
    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;
    private final PostFacadeService postFacadeService;
    private final MemberIdolQueryService memberIdolQueryService;
    private final MemberIdolCommandService memberIdolCommandService;
    private final IdolCategoryQueryService idolCategoryQueryService;
    private final EventCategoryQueryService eventCategoryQueryService;
    private final MyPageQueryService myPageQueryService;

    @Operation(summary = "홈화면 - 게시글 전체 조회 API", description = "조건이 없으면 모든 게시글을 반환하고, 조건이 있으면 해당 조건에 따라 게시글을 조회하는 API입니다." + "\n" +
            "여기서 말하는 조건은 사용자가 설정하는 필터링 조건과 지뢰 설정에 해당합니다.")
    @GetMapping("")
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostPreviewListDto> getPostList (@ValidPageNumber @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        MemberFilterDto.FilterResponseDto userFilter = myPageQueryService.getMemberFilter(userDetails.getMemberId());
        return ApiResponse.onSuccess(postQueryService.getFilteredPostList(page, userFilter.getGender(), userFilter.getMinAge(), userFilter.getMaxAge(), userDetails.getMemberId()));
    }

    @Operation(summary = "홈화면 - 관심 아이돌 목록 조회 API", description = "현재 내가 설정한 관심 있는 아이돌 목록을 조회합니다.")
    @GetMapping("/idols")
    public ApiResponse<MemberIdolResponseDto.IdolListDto> getSelectIdolResult(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<MemberIdol> memberIdolList = memberIdolQueryService.getIdolListByMember(userDetails.getMemberId());
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolListDto(memberIdolList));
    }

    @Operation(summary = "홈화면 - 게시글 아이돌 기반 조회 API", description = "해당하는 아이돌의 글만 조회하는 API 입니다." +
            "사용자가 설정한 필터링 조건과 지뢰를 통해 게시글을 조회할 수 있도록 설정했습니다.")
    @GetMapping("/idols/{idolId}")
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostPreviewListDto> getPostListByIdol (@ExistIdol @PathVariable Long idolId,
                                                                              @ValidPageNumber @RequestParam(name = "page",  defaultValue = "0") Integer page,
                                                                              @AuthenticationPrincipal CustomUserDetails userDetails){
        MemberFilterDto.FilterResponseDto userFilter = myPageQueryService.getMemberFilter(userDetails.getMemberId());
        Page<Post> postList = postQueryService.getFilteredPostListByIdol(idolId, userFilter.getGender(), userFilter.getMinAge(), userFilter.getMaxAge(), page, userDetails.getMemberId());
        return ApiResponse.onSuccess(PostConverter.postPreviewListDto(postList));
    }

    @Operation(summary = "게시글 상세 - 조회 API", description = "홈화면에서 게시글 1개 클릭시 자세히 보여주는 API입니다. wanted가 0이면 종료, 1이면 진행 중입니다. 채팅수 조회 추가 완료")
    @GetMapping("/{postId}")
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostDetailDto> getPostDetail (@ExistPost @PathVariable(name="postId") Long postId){
        return ApiResponse.onSuccess(postFacadeService.getPostDetail(postId));
    }

    @Operation(summary = "게시글 작성 - API", description = "게시글 쓰기 API입니다. 최대 5개의 이미지 업로드 가능")
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostJoinResultDto> joinPost (@AuthenticationPrincipal CustomUserDetails userDetails, @RequestPart @Valid PostRequestDto.PostJoinDto request, @Size(max = 5) @RequestPart("images") List<MultipartFile> images){
        Post post = postCommandService.joinPost(request, userDetails.getMemberId(), images);
        return ApiResponse.onSuccess(PostConverter.postJoinResultDto(post));
    }

    @Operation(summary = "게시글 작성 - 행사 종류 전체 조회 API", description = "게시글 작성하는 페이지에서 행사 목록 전체를 받아오는 API입니다.")
    @GetMapping("/events")
    public ApiResponse<List<EventCategoryResponseDto.EventCategoryDto>> getAllCategories(){
        return ApiResponse.onSuccess(eventCategoryQueryService.getGroupedCategories());
    }

    @Operation(summary = "게시글 검색 API", description = "게시글 검색 API입니다. title 기준으로 검색합니다. " +
            "사용자가 기존에 설정한 필터링 조건이 아닌, 검색할 때마다 새로운 필터 값을 적용하여 조회합니다. 다만, 지뢰 필터링은 동일하게 적용되어있습니다." +
            "필터 값(gender, minAge, maxAge)은 각각 선택적으로 적용되며, 요청 시 지정하지 않으면 전체 검색이 가능합니다.")
    @GetMapping("/search")
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostPreviewListDto> getPostListByTitle (@ValidPageNumber @RequestParam(name = "page",  defaultValue = "0") Integer page,
                                                                               @AuthenticationPrincipal CustomUserDetails userDetails,
                                                                               @RequestParam(name="searchKeyword") String searchKeyword,
                                                                               @RequestParam(name="gender", required = false) Gender gender,
                                                                               @RequestParam(name="minAge", required = false) Integer minAge,
                                                                               @RequestParam(name="maxAge", required = false) Integer maxAge){
        Page<Post> postList = postQueryService.getFilteredPostListByTitle(searchKeyword, gender, minAge, maxAge, page, userDetails.getMemberId());
        return ApiResponse.onSuccess(PostConverter.postPreviewListDto(postList));
    }

    @Operation(summary = "게시글 상세 - 게시글 상태 변경 API", description = "모집 중은 wanted가 1, 모집 완료는 0입니다.")
    @PatchMapping("/{postId}/status")
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostStatusDto> patchPostStatus(@ExistPost @PathVariable("postId") Long postId, @RequestParam("wanted") Short wanted){
        Post post = postCommandService.patchPostStatus(postId, wanted);
        return ApiResponse.onSuccess(PostConverter.postStatusDto(post));
    }

    @Operation(summary = "나의 동행 페이지- 내 게시글 조회 API", description = "나의 동행에서 내 게시글을 확인하는 API입니다.")
    @GetMapping("/my")
    @CommonApiResponses
    public ApiResponse<PostResponseDto.PostPreviewListDto> getMyPostList(@AuthenticationPrincipal CustomUserDetails userDetails, @ValidPageNumber @RequestParam(name ="page", defaultValue = "0") Integer page){
        Page<Post> postList = postQueryService.getMyPostList(userDetails.getMemberId(), page);
        return ApiResponse.onSuccess(PostConverter.postPreviewListDto(postList));
    }

    @Operation(summary = "게시글 작성 - 관심 아이돌 목록 검색 API", description = "키워드를 통해 관심있는 아이돌을 찾는 API입니다.")
    @GetMapping("/idols/search")
    public ApiResponse<IdolCategoryResponseDto.IdolListDto> getIdolListByKeyword(@RequestParam("keyword") String keyword){
        List<IdolCategory> idolCategoryList = idolCategoryQueryService.getIdolListByKeyword(keyword);
        return ApiResponse.onSuccess(IdolCategoryConverter.toIdolListDto(idolCategoryList));
    }

    @Operation(summary = "게시글 작성 - 관심 아이돌 추가 API", description = "관심 아이돌을 추가하는 API입니다.")
    @PostMapping("/idols/{idolId}")
    public ApiResponse<MemberIdolResponseDto.IdolDto> addMemberIdol(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("idolId") Long idolId){
        return ApiResponse.onSuccess(MemberIdolConverter.toIdolDto(memberIdolCommandService.addMemberIdol(userDetails.getMemberId(), idolId)));
    }
}
