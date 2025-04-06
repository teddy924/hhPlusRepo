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

    // balance
    INVALID_CHARGE_BALANCE(BAD_REQUEST.value(),"C-3001","유효하지 않은 충전 금액입니다."),

    // product
    OUT_OF_STOCK(BAD_REQUEST.value(),"C-4001","상품이 품절 상태 입니다."),
    NOT_EXIST_PRODUCT(BAD_REQUEST.value(),"C-4002","해당 상품이 존재하지 않습니다."),

    // coupon
    NOT_HAS_COUPON(BAD_REQUEST.value(),"C-5001","보유하고 있는 쿠폰이 없습니다."),
    NOT_EXIST_COUPON(BAD_REQUEST.value(),"C-5002","해당 쿠폰을 찾을 수 없습니다."),
    COUPON_SOLD_OUT(BAD_REQUEST.value(),"C-5003","선착순 마감으로 쿠폰 재고가 존재하지않습니다."),
    INVALID_COUPON(BAD_REQUEST.value(),"C-5004","유효하지 않은 쿠폰입니다."),
    DUPLICATE_ISSUE_COUPON(BAD_REQUEST.value(),"C-5005","이미 쿠폰을 받은 발급자 입니다."),

    ;

    private final int statusCode;
    private final String processCode;
    private final String message;
}