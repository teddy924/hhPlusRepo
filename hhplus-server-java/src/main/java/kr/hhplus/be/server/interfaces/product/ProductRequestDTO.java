package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "상품 요청 DTO")
public class ProductRequestDTO {
    @Schema(description = "상품 ID", example = "P00001")
    private String productId;
    @Schema(description = "카테고리", example = "TENT")
    private String category;
}
