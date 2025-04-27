package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save (Order order, PaymentInfo paymentInfo) {

        Payment payment = retrieve(order.getId()).orElse(null);

        if (payment == null) {
            payment = Payment.builder()
                    .id(null)
                    .order(order)
                    .amount(paymentInfo.amount())
                    .paymentMethod(paymentInfo.method())
                    .paymentStatus(paymentInfo.status())
                    .paidDt(paymentInfo.paidAt())
                    .sysCretDt(LocalDateTime.now())
                    .build();
        } else {
            payment = Payment.builder()
                    .id(payment.getId())
                    .order(order)
                    .amount(paymentInfo.amount())
                    .paymentMethod(paymentInfo.method())
                    .paymentStatus(paymentInfo.status())
                    .sysChgDt(LocalDateTime.now())
                    .build();
        }

        paymentRepository.save(payment);

    }

    public Optional<Payment> retrieve(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

}
