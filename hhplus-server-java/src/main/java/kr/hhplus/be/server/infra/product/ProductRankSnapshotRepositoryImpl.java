package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductRankSnapshotRepository;
import kr.hhplus.be.server.domain.product.entity.ProductRankSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRankSnapshotRepositoryImpl implements ProductRankSnapshotRepository {

    private final JpaProdcutRankSnapshotRepository jpaProdcutRankSnapshotRepository;

    @Override
    public List<ProductRankSnapshot> getByCategoryOrderByRankingAsc(String category) {
        return jpaProdcutRankSnapshotRepository.findByCategoryOrderByRankingAsc(category);
    }

    @Override
    public List<ProductRankSnapshot> getByCategoryAndSnapshotAtOrderByRankingAsc(String category, LocalDateTime snapshotAt) {
        return jpaProdcutRankSnapshotRepository.findByCategoryAndSnapshotAtOrderByRankingAsc(category, snapshotAt);
    }

    @Override
    public void deleteByCategoryAndSnapshotAt(String category, LocalDateTime snapshotAt) {
        jpaProdcutRankSnapshotRepository.deleteByCategoryAndSnapshotAt(category, snapshotAt);
    }

    @Override
    public void save(ProductRankSnapshot productRankSnapshot) {
        jpaProdcutRankSnapshotRepository.save(productRankSnapshot);
    }

    @Override
    public LocalDateTime getLatestSnapshotAt(String category) {
        return jpaProdcutRankSnapshotRepository.findLatestSnapshotAt(category);
    }
}
