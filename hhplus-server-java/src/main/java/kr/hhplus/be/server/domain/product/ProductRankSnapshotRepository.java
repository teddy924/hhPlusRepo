package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.entity.ProductRankSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRankSnapshotRepository {

    List<ProductRankSnapshot> getByCategoryOrderByRankingAsc(String category);

    List<ProductRankSnapshot> getByCategoryAndSnapshotAtOrderByRankingAsc(String category, LocalDateTime snapshotAt);

    void deleteByCategoryAndSnapshotAt(String category, LocalDateTime snapshotAt);

    void save(ProductRankSnapshot productRankSnapshot);

    LocalDateTime getLatestSnapshotAt(String category);
}
