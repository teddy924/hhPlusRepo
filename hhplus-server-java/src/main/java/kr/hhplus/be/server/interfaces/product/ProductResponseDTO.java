package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.application.product.ProductSalesResult;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import lombok.Builder;

@Schema(description = "상품 응답 DTO")
@Builder
public class ProductResponseDTO {
    private Long productId;
    private String productName;
    private Long sellerId;
    private ProductCategoryType category;
    private Long price;
    private Integer stock;
    private Integer salesQuantity;

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
                .category(ProductCategoryType.valueOf(result.category()))
                .price(result.price())
                .stock(result.stock())
                .salesQuantity(result.salesQuantity())
                .build();
    }

}
