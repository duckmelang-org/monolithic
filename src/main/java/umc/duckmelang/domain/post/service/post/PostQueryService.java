package umc.duckmelang.domain.post.service.post;

import org.springframework.data.domain.Page;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostResponseDto;

import java.util.Optional;

public interface PostQueryService {
    PostResponseDto.PostPreviewListDto getFilteredPostList(Integer page, Gender gender, Integer minAge, Integer maxAge, Long memberId);
    Page<Post> getFilteredPostListByIdol(Long idolId, Gender gender, Integer minAge, Integer maxAge, Integer page, Long memberId);
    Page<Post> getPostListByMember(Long memberId, Integer page);
    Optional<Post> getPostDetail(Long postId);
    Page<Post> getFilteredPostListByTitle(String keyword, Gender gender, Integer minAge, Integer maxAge, Integer page, Long memberId);
    Optional<Post> findById(Long postId);
    Page<Post> getMyPostList(Long memberId, Integer page);
    int getPostCount(Long memberId);
}
