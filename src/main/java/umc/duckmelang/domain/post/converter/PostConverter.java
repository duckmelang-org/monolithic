package umc.duckmelang.domain.post.converter;

import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.dto.PostDto;

@Component
public class PostConverter {

    public static Post toPost(PostDto.PostAddDto request, Member member){
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();
    }

    public static PostDto.PostAddResultDto toPostAddResultDto(Post post){
        return PostDto.PostAddResultDto.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
