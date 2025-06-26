package umc.duckmelang.domain.idolcategory.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import umc.duckmelang.domain.idolcategory.domain.IdolCategory;

import java.util.List;

@Repository
public interface IdolCategoryRepository extends JpaRepository<IdolCategory, Long> {
    @Query("SELECT i FROM IdolCategory i WHERE i.name LIKE %:keyword%")
    List<IdolCategory> findByKeyword(@Param("keyword") String keyword);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM IdolCategory i WHERE i.id = :idolId")
    int removeIdolCategoryById(@Param("idolId") Long idolId);
}
