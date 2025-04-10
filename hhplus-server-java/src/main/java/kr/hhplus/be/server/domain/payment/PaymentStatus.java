package kr.hhplus.be.server.domain.payment;

public enum PaymentStatus {

    COMPLETED ("완료"),
    CANCELLED ("취소")
    ;

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

}
