package kr.hhplus.be.server.domain.payment;

public enum PaymentMethod {

    CARD ("카드"),
    CASH ("현금"),
    BALANCE ("보유 잔액")
    ;

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}
