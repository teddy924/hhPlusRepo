package kr.hhplus.be.server.common;

public class CacheKey {

    private static final String ACCOUNT_PREFIX = "account";
    private static final String PRODUCT_PREFIX = "product";
    private static final String SNAPSHOT_RANK_KEY_PREFIX = "snapshot:rank";
    private static final String RANK_ZSET_KEY_PREFIX = "rank:zset";
    private static final String COUPON_STOCK_ZSET_KEY_PREFIX = "coupon:stock:zset";
    private static final String COUPON_ISSUE_SET_KEY_PREFIX = "coupon:issue:set";
    private static final String COUPON_ACTIVE_SET = "coupon:active:ids";
    private static final String TOTAL_KEY_PATTERN = ":*";

    public static String account(Long userId) {
        return String.format("%s:%d", ACCOUNT_PREFIX, userId);
    }

    public static String product(Long productId) {
        return String.format("%s:%d", PRODUCT_PREFIX, productId);
    }

    public static String ranking(String category) {
        return String.format("%s:%s", SNAPSHOT_RANK_KEY_PREFIX, category);
    }

    public static String category(String category) {
        return String.format("%s:%s", RANK_ZSET_KEY_PREFIX, (category == null ? "ALL" : category.toUpperCase()));
    }

    public static String stock(Long couponId) {
        return String.format("%s:%d", COUPON_STOCK_ZSET_KEY_PREFIX, couponId);
    }

    public static String getCouponStockZsetKeyPrefix() {
        return COUPON_STOCK_ZSET_KEY_PREFIX;
    }

    public static String getCouponStockZsetKeyPattern() {
        return COUPON_STOCK_ZSET_KEY_PREFIX + TOTAL_KEY_PATTERN;
    }

    public static String issuedSet(Long couponId) {
        return String.format("%s:%d", COUPON_ISSUE_SET_KEY_PREFIX, couponId);
    }

    public static String activeCouponIds(){
        return COUPON_ACTIVE_SET;
    }

}
