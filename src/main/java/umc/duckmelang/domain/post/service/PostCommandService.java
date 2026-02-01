package umc.duckmelang.domain.post.service;

import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostDto;

public interface PostCommandService {
    Post addPost(PostDto.PostAddDto request, Long memberId);
}
