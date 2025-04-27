package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductRankSnapshot;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductSalesResult(
        Long productId,
        Long sellerId,
        String productName,
        Long price,
        Integer stock,
        String category,
        LocalDateTime efctStDt,
        LocalDateTime efctFnsDt,
        Integer salesQuantity
) {
    public static ProductSalesResult from(Product product, Integer salesQuantity, String category) {
        return new ProductSalesResult(
                product.getId(),
                product.getSellerId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                category != null ? category : product.getCategory().name(),
                product.getEfctStDt(),
                product.getEfctFnsDt(),
                salesQuantity
        );
    }

    public static ProductSalesResult from(ProductRankSnapshot snapshot) {
        return new ProductSalesResult(
                snapshot.getProductId(),
                snapshot.getSellerId(),
                snapshot.getProductName(),
                snapshot.getPrice(),
                snapshot.getStock(),
                snapshot.getCategory(),
                snapshot.getEfctStDt(),
                snapshot.getEfctFnsDt(),
                snapshot.getSalesQuantity()
        );
    }
}
