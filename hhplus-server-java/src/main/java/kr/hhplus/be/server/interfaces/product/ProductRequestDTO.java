package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "상품 요청 DTO")
public record ProductRequestDTO (
        Long productId,
        String category
) {

}
