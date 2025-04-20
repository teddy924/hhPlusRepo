package kr.hhplus.be.server.config.swagger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // common
    CUSTOM_METHOD_NOT_ALLOWED(METHOD_NOT_ALLOWED.value(), "C-1001","지원하지 않은 요청입니다. 요청 정보를 다시 확인해 주시기 바랍니다."),
    CUSTOM_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR.value(), "C-1002","예상하지 않은 에러가 발생하였습니다. 관리자에게 문의해 주세요."),
    HANDLE_ERROR(BAD_REQUEST.value(),"C-1003","서비스에서 오류가 발생했습니다."),
    CLIENT_INPUT_ERROR(BAD_REQUEST.value(),"C-1004","매개변수 값을 확인해주세요."),
    INVALID_CLIENT_VALUE(BAD_REQUEST.value(),"C-1005","변수 형태를 확인해주세요."),

    // user
    INVALID_USER(BAD_REQUEST.value(),"C-2001","유효하지 않은 사용자입니다."),
    NOT_EXIST_USER(BAD_REQUEST.value(),"C-2002","사용자가 존재하지 않습니다."),

    // account
    NOT_EXIST_ACCOUNT(BAD_REQUEST.value(),"C-3001","계좌가 존재하지 않습니다."),
    INVALID_ACCOUNT_AMOUNT(BAD_REQUEST.value(),"C-3002","충전/사용 금액은 100원 단위의 100원 이상이어야 합니다."),
    INVALID_USE_AMOUNT(BAD_REQUEST.value(),"C-3003","잔액이 부족합니다."),

    // product
    OUT_OF_STOCK(BAD_REQUEST.value(),"C-4001","상품이 품절 상태 입니다."),
    NOT_EXIST_PRODUCT(BAD_REQUEST.value(),"C-4002","해당 상품이 존재하지 않습니다."),
    NOT_EXIST_PRODUCT_CATEGORY(BAD_REQUEST.value(),"C-4003","해당 상품 분류가 존재하지 않습니다."),
    INVALID_PRODUCT(BAD_REQUEST.value(),"C-4004","유효하지 않은 상품입니다."),
    INVALID_QUANTITY(BAD_REQUEST.value(),"C-4005","유효하지 않은 상품 수량 입니다."),
    FAIL_DECREASE_STOCK(BAD_REQUEST.value(),"C-4006","상품 재고 차감에 실패하였습니다."),
    FAIL_RESTORE_STOCK(BAD_REQUEST.value(),"C-4007","상품 재고 복구에 실패하였습니다."),

    // coupon
    NOT_HAS_COUPON(BAD_REQUEST.value(),"C-5001","보유하고 있는 쿠폰이 없습니다."),
    NOT_EXIST_COUPON(BAD_REQUEST.value(),"C-5002","해당 쿠폰을 찾을 수 없습니다."),
    COUPON_SOLD_OUT(BAD_REQUEST.value(),"C-5003","해당 쿠폰 재고가 존재하지 않습니다."),
    INVALID_COUPON(BAD_REQUEST.value(),"C-5004","유효하지 않은 쿠폰입니다."),
    DUPLICATE_ISSUE_COUPON(BAD_REQUEST.value(),"C-5005","이미 쿠폰을 받은 발급자 입니다."),
    ALREADY_USED_COUPON(BAD_REQUEST.value(),"C-5006","이미 사용된 쿠폰입니다."),
    INVALID_COUPON_RESTORE(BAD_REQUEST.value(),"C-5007","복구 대상 쿠폰이 아닙니다."),
    FAIL_USE_COUPON(BAD_REQUEST.value(),"C-5008","쿠폰 사용에 실패하였습니다."),
    FAIL_RESTORE_COUPON(BAD_REQUEST.value(),"C-5009","쿠폰 복구에 실패하였습니다."),

    // order,
    NOT_EXIST_ORDER(BAD_REQUEST.value(),"C-6001","해당 주문 정보를 찾을 수 없습니다."),
    NOT_EXIST_ORDER_ADDRESS(BAD_REQUEST.value(),"C-6002","해당 배송지 정보를 찾을 수 없습니다."),

    // payment
    NOT_EXIST_PAYMENT(BAD_REQUEST.value(),"C-7001","해당 결제 정보를 찾을 수 없습니다.")

    ;

    private final int statusCode;
    private final String processCode;
    private final String message;
}