package kr.hhplus.be.server.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 정보 응답 DTO")
public class OrderRetvResponseDTO {

    private Long orderId;
    private Long userId;
    private Long totalPrice;
    private Long couponId;
    private Long discountAmount;
    private Long paidAmount;
    private LocalDateTime paidAt;
    private List<OrderItemDTO> items;

}
