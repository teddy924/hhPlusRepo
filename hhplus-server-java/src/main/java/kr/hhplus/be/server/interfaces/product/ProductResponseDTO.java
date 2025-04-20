package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import lombok.Builder;

@Schema(description = "상품 Response DTO")
@Builder
public record ProductResponseDTO (
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품 명") String productName,
        @Schema(description = "판매자 ID") Long sellerId,
        @Schema(description = "상품 카테고리") ProductCategoryType category,
        @Schema(description = "가격") Long price,
        @Schema(description = "재고량") Integer stock,
        @Schema(description = "판매수량") Integer salesQuantity
) {

    public static ProductResponseDTO from(ProductResult result) {
        return ProductResponseDTO.builder()
                .productId(result.productId())
                .productName(result.productName())
                .sellerId(result.sellerId())
                .category(result.category())
                .price(result.price())
                .stock(result.stock())
                .build();
    }

    public static ProductResponseDTO from(ProductSalesResult result) {
        return ProductResponseDTO.builder()
                .productId(result.productId())
                .productName(result.productName())
                .sellerId(result.sellerId())
                .category(result.category())
                .price(result.price())
                .stock(result.stock())
                .salesQuantity(result.salesQuantity())
                .build();
    }

}
