package kr.hhplus.be.server.domain.account;

import lombok.Getter;

@Getter
public enum AccountHistType {

    CHARGE("충전")
    , USE("사용")
    , REFUND("환불")
    ;

    private final String description;

    AccountHistType(String description) {
        this.description = description;
    }
}
