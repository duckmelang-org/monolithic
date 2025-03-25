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

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @EntityGraph(attributePaths = "post")
    @Query("SELECT b.post FROM Bookmark b JOIN b.post WHERE b.member.id = :memberId")
    Page<Post> findBookmarks(Long memberId, Pageable pageable);

    boolean existsByMemberAndPost(Member member, Post post);

}
