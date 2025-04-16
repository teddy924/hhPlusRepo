package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.entity.Payment;

import java.util.Optional;

public interface PaymentRepository {

    void save(Payment payment);

    Optional<Payment> findByOrderId(Long orderId);

}
