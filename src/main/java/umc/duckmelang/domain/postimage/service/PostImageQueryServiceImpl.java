package umc.duckmelang.domain.postimage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import umc.duckmelang.domain.postimage.converter.PostImageConverter;
import umc.duckmelang.domain.postimage.domain.PostImage;
import umc.duckmelang.domain.postimage.dto.PostThumbnailResponseDto;
import umc.duckmelang.domain.postimage.repository.PostImageRepository;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.apipayload.exception.PostImageException;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostImageQueryServiceImpl implements PostImageQueryService {
    private final PostImageRepository postImageRepository;

    @Value("${spring.custom.default.profile-image}")
    private String defaultImage;

    @Override
    public PostThumbnailResponseDto getLatestPostImage(Long postId) {
        return PostImageConverter.toPostThumbnailResponseDto(postImageRepository.findFirstByPostIdOrderByCreatedAtAsc(postId)
                .orElseThrow(() -> new PostImageException(ErrorStatus.POST_IMAGE_NOT_FOUND)));
    }

    //프로필에서 대표로 보일 이미지 조회
    @Override
    public Page<PostThumbnailResponseDto> getPostsImage(Long memberId, int page) {
        return postImageRepository.findLatestPostImagesForMemberPosts(memberId, PageRequest.of(page, 10));
    }

    @Override
    public Page<PostThumbnailResponseDto> getMyPostsImage(Long memberId, int page) {
        return postImageRepository.findLatestPostImagesForMemberPosts(memberId, PageRequest.of(page, 10));
    }

    @Override
    public Map<Long, String> getFirstImageUrlsForPosts(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 조회된 이미지들을 Map으로 변환
        Map<Long, String> imageMap = postImageRepository.findFirstImagesForPosts(postIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getPost().getId(),
                        PostImage::getPostImageUrl,
                        (a, b) -> a
                ));

        // 최종 결과 생성
        return postIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        postId -> imageMap.getOrDefault(postId, defaultImage)
                ));
    }
}
