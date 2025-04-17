CREATE PROCEDURE insert_coupons()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 10 DO
        INSERT INTO coupon (
            name, discount_type, discount_value,
            limit_quantity, remain_quantity, efct_st_dt, efct_fns_dt
        )
        VALUES (
            CONCAT('Coupon_', i),
            IF(i % 2 = 0, 'AMOUNT', 'RATE'),
            IF(i % 2 = 0, 3000, 10),
            1000,
            1000,
            NOW(),
            DATE_ADD(NOW(), INTERVAL 30 DAY)
        );
        SET i = i + 1;
    END WHILE;
END;

CREATE PROCEDURE insert_coupon_issues()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000 DO
        INSERT INTO coupon_issue (user_id, coupon_id, status)
        VALUES (
            i,
            (i MOD 10) + 1,
            'ISSUED'
        );
        SET i = i + 1;
    END WHILE;
END;