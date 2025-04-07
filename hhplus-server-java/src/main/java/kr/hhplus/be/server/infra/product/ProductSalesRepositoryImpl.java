package kr.hhplus.be.server.infra.product;

import kr.hhplus.be.server.domain.product.ProductSalesDto;
import kr.hhplus.be.server.domain.product.ProductSalesRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductSalesRepositoryImpl implements ProductSalesRepository {

    @Override
    public List<ProductSalesDto> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit) {
        return List.of();
    }

    @Override
    public List<ProductSalesDto> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit, String category) {
        return List.of();
    }

}



