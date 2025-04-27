INSERT INTO orders (id, user_id, total_amount, order_status, sys_cret_dt)
SELECT
    id,
    FLOOR(1 + RAND() * 1000),                    -- user_id
    15000 + (id % 1000),                         -- total_amount
    ELT((id % 3) + 1, 'CREATED', 'PAID', 'CANCELED'), -- order_status
    DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM (
         SELECT @rownum_o := @rownum_o + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             information_schema.tables t3,
             (SELECT @rownum_o := 0) r
             LIMIT 10000
     ) tmp;

INSERT INTO order_item (order_id, product_id, quantity, total_amount, sys_cret_dt)
SELECT
    order_id,
    1 + FLOOR(RAND() * 105),           -- product_id (1~105)
    1 + FLOOR(RAND() * 5),             -- quantity (1~5)
    (1 + FLOOR(RAND() * 5)) * (1000 + ((1 + FLOOR(RAND() * 105)) * 10)),  -- total_amount
    NOW()
FROM (
         SELECT id AS order_id FROM (
                                        SELECT @rownum_oi := @rownum_oi + 1 AS id
                                        FROM information_schema.tables t1,
                                            information_schema.tables t2,
                                            information_schema.tables t3,
                                            (SELECT @rownum_oi := 0) r
                                            LIMIT 10000
                                    ) orders
                                        CROSS JOIN (
             SELECT n FROM (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3) nums
         ) cnt
         WHERE cnt.n <= 1 + (orders.id % 3)
     ) tmp;

INSERT INTO order_address (order_id, receiver_name, phone, address1, address2, zipcode, memo, sys_cret_dt)
SELECT
    id,
    CONCAT('수령인_', id),
    '01012345678',
    '서울시 강남구',
    CONCAT('테스트동 ', id, '호'),
    CONCAT('062', LPAD(id % 1000, 3, '0')),
    '문 앞에 두세요',
    NOW()
FROM (
         SELECT @rownum_oa := @rownum_oa + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             information_schema.tables t3,
             (SELECT @rownum_oa := 0) r
             LIMIT 10000
     ) tmp;

INSERT INTO order_history (order_id, status, memo, sys_cret_dt)
SELECT
    id,
    ELT((id % 3) + 1, 'CREATED', 'PAID', 'CANCELED'),
    CONCAT('상태 로그_', id),
    DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM (
         SELECT @rownum_oh := @rownum_oh + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             information_schema.tables t3,
             (SELECT @rownum_oh := 0) r
             LIMIT 10000
     ) tmp;

INSERT INTO order_coupon (order_id, coupon_issue_id, discount_amount, used_dt, sys_cret_dt)
SELECT
    id,
    1 + (id % 1000),
    1000 + (id % 2000),
    NOW(),
    NOW()
FROM (
         SELECT @rownum_oc := @rownum_oc + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             information_schema.tables t3,
             (SELECT @rownum_oc := 0) r
             LIMIT 10000
     ) tmp
WHERE id % 3 = 0;

INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_dt, sys_cret_dt)
SELECT
    id,
    15000 + (id % 1000),
    ELT((id % 3) + 1, 'CARD', 'CASH', 'BALANCE'),
    IF(ELT((id % 3) + 1, 'CREATED', 'PAID', 'CANCELED') = 'PAID', 'COMPLETED', 'CANCELLED'),
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    DATE_SUB(NOW(), INTERVAL 2 DAY)
FROM (
         SELECT @rownum_p := @rownum_p + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             information_schema.tables t3,
             (SELECT @rownum_p := 0) r
             LIMIT 10000
     ) tmp
WHERE ELT((id % 3) + 1, 'CREATED', 'PAID', 'CANCELED') IN ('PAID', 'CANCELED');
