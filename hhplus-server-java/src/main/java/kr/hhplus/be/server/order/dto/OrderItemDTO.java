package kr.hhplus.be.server.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 상품 정보 DTO")
public class OrderItemDTO {

    private Long productId;
    private String productName;
    private int quantity;
    private Long unitPrice;

}
