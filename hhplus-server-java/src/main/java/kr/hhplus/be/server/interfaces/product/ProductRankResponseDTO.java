package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.product.ProductRankResult;
import lombok.Builder;

@Schema(description = "상품 랭킹 응답 DTO")
@Builder
public class ProductRankResponseDTO {

    Long productId;
    int saleQuantity;

    public static ProductRankResponseDTO from (ProductRankResult productRankResult) {
        return ProductRankResponseDTO.builder()
                .productId(productRankResult.productId())
                .saleQuantity(productRankResult.saleQuantity())
                .build();
    }

}
