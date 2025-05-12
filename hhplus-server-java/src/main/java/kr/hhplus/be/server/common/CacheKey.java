package kr.hhplus.be.server.common;

public class CacheKey {

    private static final String ACCOUNT_PREFIX = "account";
    private static final String PRODUCT_PREFIX = "product";

    public static String account(Long userId) {
        return String.format("%s:%d", ACCOUNT_PREFIX, userId);
    }

    public static String product(Long productId) {
        return String.format("%s:%d", PRODUCT_PREFIX, productId);
    }
}
