package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 응답 DTO")
public class ProductResponseDTO {
    private String productId;
    private String productName;
    private Long sellerId;
    private String category;
    private Long price;
    private Integer stock;
}
