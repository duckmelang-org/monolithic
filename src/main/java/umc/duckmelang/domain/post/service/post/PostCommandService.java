package umc.duckmelang.domain.post.service.post;

import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostRequestDto;

import java.util.*;

public interface PostCommandService {
    Post joinPost(PostRequestDto.PostJoinDto request, Long memberId);
    Post joinPost(PostRequestDto.PostJoinDto request, Long memberId, List<MultipartFile> postImages);
    Post patchPostStatus(Long postId, Short wanted);
    void deleteMyPost(Long postId);
    Post patchPost(Long postId, PostRequestDto.PostJoinDto request, List<MultipartFile> images);

}
