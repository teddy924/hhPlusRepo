package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSalesRepository {

    List<ProductSalesDto> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit);

    List<ProductSalesDto> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit, String category);

}
