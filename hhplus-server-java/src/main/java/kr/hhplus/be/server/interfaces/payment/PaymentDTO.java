package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Schema(description = "결제 DTO")
public record PaymentDTO(
        @Schema(description = "결제 금액") Long amount,
        @Schema(description = "결제 방법") PaymentMethod method,
        @Schema(description = "결제 상태") PaymentStatus status,
        @Schema(description = "결제 일자") LocalDateTime paidAt
) {
    public static PaymentDTO from(Payment payment) {
        return new PaymentDTO(
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getPaidDt()
        );
    }
}