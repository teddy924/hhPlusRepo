package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.application.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public void save(PaymentInfo paymentInfo) {

    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return Optional.empty();
    }
}
