package kr.hhplus.be.server.common;

public class CacheKey {

    private static final String ACCOUNT_PREFIX = "account";
    private static final String PRODUCT_PREFIX = "product";
    private static final String RANKING_REDIS_KEY_PREFIX = "snapshot:rank";

    public static String account(Long userId) {
        return String.format("%s:%d", ACCOUNT_PREFIX, userId);
    }

    public static String product(Long productId) {
        return String.format("%s:%d", PRODUCT_PREFIX, productId);
    }

    public static String ranking(String category) {return String.format("%s:%s", RANKING_REDIS_KEY_PREFIX, category);}
}
