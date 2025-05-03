package kr.hhplus.be.server.common.exception;

public class LockAcquireFailException extends RuntimeException {

    public LockAcquireFailException(String message) {
        super(message);
    }

    public LockAcquireFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockAcquireFailException() {
        super("분산 락 획득에 실패했습니다.");
    }
}
