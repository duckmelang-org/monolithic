package umc.duckmelang.domain.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.application.domain.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
