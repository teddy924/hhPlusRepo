package kr.hhplus.be.server.domain.payment;

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