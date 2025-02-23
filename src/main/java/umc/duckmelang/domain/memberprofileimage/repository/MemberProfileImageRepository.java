package umc.duckmelang.domain.memberprofileimage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.memberprofileimage.domain.MemberProfileImage;
import umc.duckmelang.domain.postimage.domain.PostImage;

@Repository
public interface MemberProfileImageRepository extends JpaRepository<MemberProfileImage, Long> {
    Optional<MemberProfileImage> findFirstByMemberIdAndIsPublicTrueOrderByCreatedAtAsc(Long memberId);
    Page<MemberProfileImage> findAllByMemberIdAndMemberImageNot(Long memberId, String memberImage, Pageable pageable);
    Page<MemberProfileImage> findAllByIsPublicIsTrueAndMemberIdAndMemberImageNot(Long memberId, String memberImage, Pageable pageable);

    @Query("SELECT mpi FROM MemberProfileImage mpi WHERE (mpi.member.id, mpi.createdAt) IN " +
            "(SELECT mp.member.id, MIN(mp.createdAt) FROM MemberProfileImage mp WHERE mp.isPublic = true and mp.member.id IN :memberIds GROUP BY mp.member.id)")
    List<MemberProfileImage> findFirstProfileImagesForMembers(@Param("memberIds") List<Long> memberIds);
}
