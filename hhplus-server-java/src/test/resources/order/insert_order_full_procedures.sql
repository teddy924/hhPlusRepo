CREATE PROCEDURE insert_orders_and_related()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE item_count INT;
    DECLARE product_id INT;
    DECLARE quantity INT;
    DECLARE order_status VARCHAR(20);
    DECLARE coupon_issue_id INT;
    DECLARE discount_amount INT;
    DECLARE payment_method VARCHAR(20);
    DECLARE payment_status VARCHAR(20);

    WHILE i <= 10000 DO
        SET order_status = ELT((i % 3) + 1, 'CREATED', 'PAID', 'CANCELED');

        INSERT INTO orders (id, user_id, total_amount, order_status, sys_cret_dt)
        VALUES (
            i,
            FLOOR(1 + RAND() * 1000),
            15000 + (i % 1000),
            order_status,
            DATE_SUB(NOW(), INTERVAL 2 DAY)
        );

        -- 주문 아이템 1~3개씩 생성
        SET item_count = 1 + (i % 3);
        WHILE item_count > 0 DO
            SET product_id = 1 + FLOOR(RAND() * 105);
            SET quantity = 1 + FLOOR(RAND() * 5);
            INSERT INTO order_item (order_id, product_id, quantity, total_amount, sys_cret_dt)
            VALUES (
                i, product_id, quantity,
                quantity * (1000 + (product_id * 10)),
                NOW()
            );
            SET item_count = item_count - 1;
        END WHILE;

        -- 주문 주소
        INSERT INTO order_address (order_id, receiver_name, phone, address1, address2, zipcode, memo, sys_cret_dt)
        VALUES (
            i, CONCAT('수령인_', i), '01012345678', '서울시 강남구', CONCAT('테스트동 ', i, '호'), CONCAT('062', LPAD(i % 1000, 3, '0')), '문 앞에 두세요', NOW()
        );

        -- 주문 이력
        INSERT INTO order_history (order_id, status, memo, sys_cret_dt)
        VALUES (
            i, order_status, CONCAT('상태 로그_', i), DATE_SUB(NOW(), INTERVAL 2 DAY)
        );

        -- 쿠폰은 3건마다 1건씩 적용
        IF (i % 3 = 0) THEN
            SET coupon_issue_id = 1 + (i % 1000);
            SET discount_amount = 1000 + (i % 2000);
            INSERT INTO order_coupon (order_id, coupon_issue_id, discount_amount, used_dt, sys_cret_dt)
            VALUES (i, coupon_issue_id, discount_amount, NOW(), NOW());
        END IF;

        -- 결제 정보 생성 (PAID/CANCELED 상태만)
        IF order_status IN ('PAID', 'CANCELED') THEN
            SET payment_method = ELT((i % 3) + 1, 'CARD', 'CASH', 'BALANCE');
            SET payment_status = IF(order_status = 'PAID', 'COMPLETED', 'CANCELLED');
            INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_dt, sys_cret_dt)
            VALUES (i, 15000 + (i % 1000), payment_method, payment_status, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));
        END IF;

        SET i = i + 1;
    END WHILE;
END;