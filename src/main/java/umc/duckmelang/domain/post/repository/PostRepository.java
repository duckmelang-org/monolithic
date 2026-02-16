package umc.duckmelang.domain.post.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.duckmelang.domain.post.domain.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 데이터베이스 수준에서 쓰기 락(Exclusive Lock)을 건다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findByIdWithPessimisticLock(@Param("id") Long id);
}
