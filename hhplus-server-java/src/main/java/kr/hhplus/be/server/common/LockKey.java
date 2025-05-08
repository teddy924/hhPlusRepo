package kr.hhplus.be.server.common;

public class LockKey {

    private static final String LOCK_ACCOUNT_PREFIX = "lock:account";
    private static final String LOCK_PRODUCT_PREFIX = "lock:product";

    public static String account(Long userId) {
        return String.format("%s:%d", LOCK_ACCOUNT_PREFIX, userId);
    }

    public static String product(Long productId) {
        return String.format("%s:%d", LOCK_PRODUCT_PREFIX, productId);
    }
}
