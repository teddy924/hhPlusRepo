package kr.hhplus.be.server.domain.order;

public enum OrderStatus {


    CREATED ("생성"),
    PAID ("결제 완료"),
    CANCELED ("주문 취소")
    ;

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

}
