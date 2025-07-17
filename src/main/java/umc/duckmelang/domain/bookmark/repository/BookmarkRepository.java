package umc.duckmelang.domain.bookmark.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.bookmark.domain.Bookmark;
import umc.duckmelang.domain.member.domain.Member;
import umc.duckmelang.domain.post.domain.Post;
import java.util.Optional;


@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    void deleteAllByMember(Member member);

    @EntityGraph(attributePaths = "post")
    @Query("SELECT b FROM Bookmark b WHERE b.member.id = :memberId")
    Page<Bookmark> findBookmarks(Long memberId, Pageable pageable);

    Optional<Bookmark> findByMemberAndPost(Member member, Post post);

    boolean existsByMemberAndPost(Member member, Post post);
}
