INSERT INTO product (id, seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt, sys_cret_dt)
VALUES (90001, 1, '재고부족상품', 100, 10, 'TENT', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW());

INSERT INTO users (id, email, password, name, phone) VALUES
(80001, 'lowmoney1@test.com', 'pw', '잔액풍족', '01011112222');
INSERT INTO account (id, user_id, balance, sys_cret_dt)
VALUES (80001,80001, 10000000, NOW());
INSERT INTO coupon_issue (id, user_id, coupon_id, status, issued_dt, used_dt)
VALUES (80001, 80001, 4, 'USED', NOW(), NOW());

INSERT INTO users (id, email, password, name, phone) VALUES
(80002, 'lowmoney2@test.com', 'pw', '잔액부족', '01011112222');

INSERT INTO account (id, user_id, balance, sys_cret_dt)
VALUES (80002,80002, 0, NOW());

-- -- 사용된 쿠폰 발급 이력
-- INSERT INTO coupon (id, name, discount_type, discount_value, limit_quantity, remain_quantity, efct_st_dt, efct_fns_dt, sys_cret_dt)
-- VALUES (90001L, '테스트쿠폰', 'AMOUNT', 1000, 100, 100, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW());
--
-- INSERT INTO coupon_issue (id, user_id, coupon_id, status, issued_dt, used_dt)
-- VALUES (90001L, 90001L, 90001L, 'USED', NOW(), NOW());
--
-- -- 주문 + 아이템 + 주소 + 결제 (조회 대상)
-- INSERT INTO orders (id, user_id, total_amount, order_status, sys_cret_dt)
-- VALUES (2000001L, 90001L, 10000L, 'PAID', NOW());
--
-- INSERT INTO order_item (order_id, product_id, quantity, total_amount, sys_cret_dt)
-- VALUES (2000001L, 1, 1, 10000L, NOW());
--
-- INSERT INTO order_address (order_id, receiver_name, phone, address1, address2, zipcode, memo, sys_cret_dt)
-- VALUES (2000001L, '조회용수령인', '01033334444', '서울시 테스트구', '101동', '12345', '문 앞', NOW());
--
-- INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_dt, sys_cret_dt)
-- VALUES (2000001L, 10000L, 'BALANCE', 'COMPLETED', NOW(), NOW());
--
-- -- 쿠폰 미사용 상태도 삽입
-- INSERT INTO coupon (id, name, discount_type, discount_value, limit_quantity, remain_quantity, efct_st_dt, efct_fns_dt, sys_cret_dt)
-- VALUES (90002L, '미사용쿠폰', 'AMOUNT', 2000, 100, 100, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW());
--
-- INSERT INTO coupon_issue (id, user_id, coupon_id, status, issued_dt)
-- VALUES (90002L, 90001L, 90002L, 'ISSUED', NOW());
