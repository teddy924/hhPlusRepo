package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.product.ProductResult;
import kr.hhplus.be.server.domain.product.ProductCategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 응답 DTO")
public class ProductResponseDTO {
    private Long productId;
    private String productName;
    private Long sellerId;
    private ProductCategoryType category;
    private Long price;
    private Integer stock;

    public static ProductResponseDTO from(ProductResult queryDto) {
        return new ProductResponseDTO(
                queryDto.productId()
                , queryDto.productName()
                , queryDto.sellerId()
                , queryDto.category()
                , queryDto.price()
                , queryDto.stock()
        );
    }

}
