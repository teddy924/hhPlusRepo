package kr.hhplus.be.server.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 결제 응답 DTO")
public class OrderResponseDTO {

    private Long orderId;
    private boolean success;
    private Long remainingAccount;
    private Long paidAmount;
    private LocalDateTime paidAt;
    private String message;

}
