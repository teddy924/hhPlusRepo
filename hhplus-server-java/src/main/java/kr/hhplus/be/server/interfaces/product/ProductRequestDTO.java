package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "상품 Request DTO")
@Builder
public record ProductRequestDTO (
        @Schema(description = "상품 ID") Long productId,
        @Schema(description = "상품 카테고리") String category
) {

}
