package umc.duckmelang.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.duckmelang.domain.application.domain.MateRelationship;

public interface MateRelationshipRepository extends JpaRepository<MateRelationship, Long> {
}
