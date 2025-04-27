package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.entity.ProductRankSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaProdcutRankSnapshotRepository extends JpaRepository<ProductRankSnapshot, Long> {

    List<ProductRankSnapshot> findByCategoryOrderByRankingAsc(String category);

    List<ProductRankSnapshot> findByCategoryAndSnapshotAtOrderByRankingAsc(String category, LocalDateTime snapshotAt);

    void deleteByCategoryAndSnapshotAt(String category, LocalDateTime snapshotAt);

    @Query("SELECT MAX(s.snapshotAt) FROM ProductRankSnapshot s " +
            "WHERE (:category IS NULL AND s.category IS NULL OR s.category = :category)")
    LocalDateTime findLatestSnapshotAt(@Param("category") String category);
}
