package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.ProductSalesResult;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductSalesRepository {

    List<ProductSalesResult> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit);

    List<ProductSalesResult> findTopRankBySales(LocalDateTime start, LocalDateTime end, int limit, String category);

}
