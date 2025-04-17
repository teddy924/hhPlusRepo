package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.ProductCategoryType;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProductResult(
        Long productId
        , Long sellerId
        , String productName
        , Long price
        , Integer stock
        , ProductCategoryType category
        , LocalDateTime efctStDt
        , LocalDateTime efctFnsDt
) {
    public static ProductResult from(Product product) {
        return new ProductResult(
                product.getId()
                , product.getSellerId()
                , product.getName()
                , product.getPrice()
                , product.getStock()
                , product.getCategory()
                , product.getEfctStDt()
                , product.getEfctFnsDt()
        );
    }
}
