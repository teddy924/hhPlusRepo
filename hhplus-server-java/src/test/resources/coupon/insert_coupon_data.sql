INSERT INTO coupon (
    name, discount_type, discount_value,
    limit_quantity, remain_quantity, efct_st_dt, efct_fns_dt
)
SELECT
    CONCAT('Coupon_', id),
    IF(id % 2 = 0, 'AMOUNT', 'RATE'),
    IF(id % 2 = 0, 3000, 10),
    1000,
    1000,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM (
         SELECT @rownum_c := @rownum_c + 1 AS id
         FROM information_schema.tables t1,
             (SELECT @rownum_c := 0) r
             LIMIT 10
     ) tmp;

INSERT INTO coupon_issue (user_id, coupon_id, status)
SELECT
    id,                            -- user_id (1~1000)
    ( (id - 1) % 10 ) + 1,         -- coupon_id (1~10을 반복)
    'ISSUED'
FROM (
    SELECT @rownum_ci := @rownum_ci + 1 AS id
    FROM information_schema.tables t1,
    information_schema.tables t2,
    (SELECT @rownum_ci := 0) r
    LIMIT 1000
    ) tmp;
