package kr.hhplus.be.server.domain.coupon;

public enum CouponDiscountType {
    AMOUNT ("금액 할인")
    , RATE ("비율 할인")
    ;

    private final String description;

    CouponDiscountType(String description) {
        this.description = description;
    }

}
