package umc.duckmelang.domain.uuid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import umc.duckmelang.domain.uuid.domain.Uuid;

public interface UuidRepository extends JpaRepository<Uuid, String> {
    boolean existsByUuid(String uuid);

    void deleteByUuid(String uuid);
}
