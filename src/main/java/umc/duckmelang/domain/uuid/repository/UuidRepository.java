package umc.duckmelang.domain.uuid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.uuid.domain.Uuid;

public interface UuidRepository extends JpaRepository<Uuid, String> {
}
