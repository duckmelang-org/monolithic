package umc.duckmelang.domain.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.member.domain.enums.Gender;
import umc.duckmelang.domain.post.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE " +
            "(:gender IS NULL OR p.member.gender = :gender) AND " +
            "(:minAge IS NULL OR TIMESTAMPDIFF(YEAR, p.member.birth, CURRENT_DATE) >= :minAge) AND " +
            "(:maxAge IS NULL OR TIMESTAMPDIFF(YEAR, p.member.birth, CURRENT_DATE) <= :maxAge) AND " +
            "NOT EXISTS (SELECT 1 FROM Landmine l WHERE l.member = :member AND p.title LIKE CONCAT('%', l.content, '%'))")
    Page<Post> findFilteredPosts(@Param("gender") Gender gender,
                                 @Param("minAge") Integer minAge,
                                 @Param("maxAge") Integer maxAge,
                                 @Param("member") Member member,
                                 Pageable pageable);


    @EntityGraph(attributePaths = {"postIdolList", "postIdolList.idolCategory"})
    @Query("SELECT p FROM Post p " +
            "JOIN p.postIdolList pi " +
            "JOIN pi.idolCategory ic " +
            "WHERE ic.id = :idolId AND " +
            "(:gender IS NULL OR p.member.gender = :gender) AND " +
            "(:minAge IS NULL OR TIMESTAMPDIFF(YEAR, p.member.birth, CURRENT_DATE) >= :minAge) AND " +
            "(:maxAge IS NULL OR TIMESTAMPDIFF(YEAR, p.member.birth, CURRENT_DATE) <= :maxAge) AND " +
            "NOT EXISTS (SELECT 1 FROM Landmine l WHERE l.member = :member AND p.title LIKE CONCAT('%', l.content, '%'))")
    Page<Post> findFilteredPostsByIdol(@Param("idolId") Long idolId,
                                       @Param("gender") Gender gender,
                                       @Param("minAge") Integer minAge,
                                       @Param("maxAge") Integer maxAge,
                                       @Param("member") Member member,
                                       Pageable pageable);


    @Query("SELECT p FROM Post p JOIN FETCH p.member pm WHERE pm.id = :memberId")
    Page<Post> findByMember(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "p.title LIKE CONCAT('%', :keyword, '%') AND " +
            "(:gender IS NULL OR p.member.gender = :gender) AND " +
            "(:minAge IS NULL OR TIMESTAMPDIFF(YEAR, p.member.birth, CURRENT_DATE) >= :minAge) AND " +
            "(:maxAge IS NULL OR TIMESTAMPDIFF(YEAR, p.member.birth, CURRENT_DATE) <= :maxAge) AND " +
            "NOT EXISTS (SELECT 1 FROM Landmine l WHERE l.member = :member AND p.title LIKE CONCAT('%', l.content, '%'))")
    Page<Post> findFilteredPostsByTitle(@Param("keyword") String keyword,
                                        @Param("gender") Gender gender,
                                        @Param("minAge") Integer minAge,
                                        @Param("maxAge") Integer maxAge,
                                        @Param("member") Member member,
                                        Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId")
    Page<Post> findMyPost(Long memberId, Pageable pageable);

    int countAllByMemberId(Long memberId);

}
