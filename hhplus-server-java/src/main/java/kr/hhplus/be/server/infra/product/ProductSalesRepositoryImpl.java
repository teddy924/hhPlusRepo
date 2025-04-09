package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.domain.product.ProductSalesRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductSalesRepositoryImpl implements ProductSalesRepository {

    @Override
    public List<ProductSalesResult> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit) {
        return List.of();
    }

    @Override
    public List<ProductSalesResult> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit, String category) {
        return List.of();
    }

}



