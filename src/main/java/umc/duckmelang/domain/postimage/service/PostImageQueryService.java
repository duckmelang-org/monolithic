package umc.duckmelang.domain.postimage.service;

import java.util.Map;
import java.util.*;
import org.springframework.data.domain.Page;
import umc.duckmelang.domain.postimage.domain.PostImage;
import umc.duckmelang.domain.postimage.dto.PostThumbnailResponseDto;

public interface PostImageQueryService {

    PostThumbnailResponseDto getLatestPostImage(Long postId);

    Page<PostThumbnailResponseDto> getPostsImage(Long memberId, int page);

    //추후 memberId가 아닌 jwt 사용
    Page<PostThumbnailResponseDto> getMyPostsImage(Long memberId, int page);

    Map<Long, String> getFirstImageUrlsForPosts(List<Long> postIds);
}
