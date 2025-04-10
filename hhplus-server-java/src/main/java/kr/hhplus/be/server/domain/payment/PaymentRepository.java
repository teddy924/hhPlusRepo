package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.application.payment.PaymentInfo;

public interface PaymentRepository {

    void save(PaymentInfo paymentInfo);

}
