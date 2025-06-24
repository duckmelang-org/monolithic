package umc.duckmelang.domain.post.converter;

import org.springframework.data.domain.Page;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.domain.PostImage;
import umc.duckmelang.domain.post.dto.PostImageResponseDto;
import umc.duckmelang.domain.post.dto.PostThumbnailResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class PostImageConverter {

    public static PostImageResponseDto.PostThumbnailListResponseDto toPostThumbnailListResponseDto(Page<PostThumbnailResponseDto> thumbnails){
        List<PostThumbnailResponseDto> thumbnailList = thumbnails.stream().collect(Collectors.toList());
        return PostImageResponseDto.PostThumbnailListResponseDto.builder()
                .PostImagesList(thumbnailList)
                .listSize((thumbnailList.size()))
                .totalPage(thumbnails.getTotalPages())
                .totalElements(thumbnails.getTotalElements())
                .isFirst(thumbnails.isFirst())
                .isLast(thumbnails.isLast())
                .build();
    }

    public static PostThumbnailResponseDto toPostThumbnailResponseDto(PostImage postImage){
        return PostThumbnailResponseDto.builder()
                .postId(postImage.getPost().getId())
                .postImageUrl(postImage.getPostImageUrl())
                .createdAt(postImage.getCreatedAt())
                .build();
    }
}
