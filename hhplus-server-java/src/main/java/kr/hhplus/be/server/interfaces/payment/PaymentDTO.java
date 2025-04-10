package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;

import java.time.LocalDateTime;

public record PaymentDTO(
        Long amount,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime paidAt
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