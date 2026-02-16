package umc.duckmelang.domain.post.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.domain.type.PostStatus;
import umc.duckmelang.domain.post.dto.PostDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostConverter {

    public static Post toPost(PostDto.PostAddDto request, Member member){
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .maxParticipants(request.getMaxParticipants())
                .currentParticipants(0)
                .postStatus(PostStatus.RECRUITING)
                .build();
    }

    public static PostDto.PostAddResultDto toPostAddResultDto(Post post){
        return PostDto.PostAddResultDto.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 상세 조회
    public static PostDto.PostDetailDto toPostDetailDto(Post post){
        return PostDto.PostDetailDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getMember().getNickname())
                .maxParticipants(post.getMaxParticipants())
                .currentParticipants(post.getCurrentParticipants())
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 리스트 아이템
    public static PostDto.PostListItemDto toPostListItemDto(Post post){
        return PostDto.PostListItemDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .nickname(post.getMember().getNickname())
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 리스트
    public static PostDto.PostListDto toPostListDto(Page<Post> postPage){
        List<PostDto.PostListItemDto> postListItemDtoList = postPage.getContent().stream()
                .map(PostConverter::toPostListItemDto)
                .collect(Collectors.toList());

        return PostDto.PostListDto.builder()
                .postListItemDtoList(postListItemDtoList)
                .listSize(postListItemDtoList.size())
                .totalPage(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .isFirst(postPage.isFirst())
                .isLast(postPage.isLast())
                .build();
    }
}
