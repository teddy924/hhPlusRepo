package kr.hhplus.be.server.common.exception;

import kr.hhplus.be.server.config.swagger.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final String processCode;

    public CustomException(ErrorCode code) {
        super(code.getMessage());
        this.processCode = code.getProcessCode();
    }

    public CustomException(String processCode, String message) {
        super(message);
        this.processCode = processCode;
    }
}