package umc.duckmelang.domain.postimage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.postimage.domain.PostImage;
import umc.duckmelang.domain.postimage.dto.PostThumbnailResponseDto;
import java.util.*;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    @Query("SELECT new umc.duckmelang.domain.postimage.dto.PostThumbnailResponseDto(p.id, pi.postImageUrl, pi.createdAt) " +
            "FROM PostImage pi " +
            "JOIN pi.post p " +
            "WHERE p.member.id = :memberId " +
            "AND NOT EXISTS (SELECT 1 FROM PostImage pi2 WHERE pi2.post = p AND pi2.createdAt > pi.createdAt)")
    Page<PostThumbnailResponseDto> findLatestPostImagesForMemberPosts(@Param("memberId") Long memberId, Pageable pageable);

    Optional<PostImage> findFirstByPostIdOrderByCreatedAtAsc(Long postId);

    @Query("SELECT pi FROM PostImage pi WHERE (pi.post.id, pi.createdAt) IN " +
            "(SELECT p.post.id, MIN(p.createdAt) FROM PostImage p WHERE p.post.id IN :postIds GROUP BY p.post.id)")
    List<PostImage> findFirstImagesForPosts(@Param("postIds") List<Long> postIds);
}


