package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.payment.PaymentMethod;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PaymentInfo(
        Long orderId,
        Long amount,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime paidAt
) {}