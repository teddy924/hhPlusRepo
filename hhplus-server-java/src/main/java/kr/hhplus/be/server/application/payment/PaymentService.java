package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.config.swagger.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public void save (Order order, PaymentInfo paymentInfo) {

        Payment payment = new Payment();

        if (paymentInfo.status() == PaymentStatus.COMPLETED) {
            payment = Payment.builder()
                    .order(order)
                    .amount(paymentInfo.amount())
                    .paymentMethod(paymentInfo.method())
                    .paymentStatus(paymentInfo.status())
                    .paidDt(paymentInfo.paidAt())
                    .sysCretDt(LocalDateTime.now())
                    .build();
        } else if (paymentInfo.status() == PaymentStatus.CANCELLED) {
            payment = Payment.builder()
                    .order(order)
                    .paymentStatus(paymentInfo.status())
                    .sysChgDt(LocalDateTime.now())
                    .build();
        }

        paymentRepository.save(payment);

    }

    public Payment retrieveByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_PAYMENT));
    }

}
