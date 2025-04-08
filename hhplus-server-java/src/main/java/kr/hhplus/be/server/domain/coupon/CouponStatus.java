package kr.hhplus.be.server.domain.coupon;

public enum CouponStatus {

    ISSUED ("발급")
    , USED ("사용완료")
    , EXPIRED ("만료")
    ;

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }
}
