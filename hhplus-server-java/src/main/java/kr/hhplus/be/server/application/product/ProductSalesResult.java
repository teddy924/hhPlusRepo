package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductSalesResult(
        Long productId,
        Long sellerId,
        String productName,
        Long price,
        Integer stock,
        ProductCategoryType category,
        LocalDateTime efctStDt,
        LocalDateTime efctFnsDt,
        Integer salesQuantity
) {
    public static ProductSalesResult from(Product product, Integer salesQuantity) {
        return new ProductSalesResult(
                product.getId(),
                product.getSellerId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getEfctStDt(),
                product.getEfctFnsDt(),
                salesQuantity
        );
    }

}
