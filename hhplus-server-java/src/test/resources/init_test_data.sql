INSERT INTO product (id, seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt, sys_cret_dt)
VALUES (900001, 1, '재고부족상품', 100, 10, 'TENT', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), NOW());

INSERT INTO users (id, email, password, name, phone) VALUES
(800001, 'lowmoney1@test.com', 'pw', '잔액풍족', '01011112222');
INSERT INTO account (id, user_id, balance, sys_cret_dt)
VALUES (800001,800001, 10000000, NOW());
INSERT INTO coupon_issue (id, user_id, coupon_id, status, issued_dt, used_dt)
VALUES (800001, 800001, 4, 'USED', NOW(), NOW());

INSERT INTO users (id, email, password, name, phone) VALUES
(800002, 'lowmoney2@test.com', 'pw', '잔액부족', '01011112222');

INSERT INTO account (id, user_id, balance, sys_cret_dt)
VALUES (800002,80002, 0, NOW());

INSERT INTO coupon (id, name, discount_type, discount_value, limit_quantity, remain_quantity, efct_st_dt, efct_fns_dt, sys_cret_dt)
VALUES (700001, '테스트쿠폰', 'AMOUNT', 1000, 100, 5, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));

INSERT INTO product (id, seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt, sys_cret_dt)
VALUES (900002, 1, '상위상품동시성테스트상품', 1000, 200000, 'TENT', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));
INSERT INTO orders (id, user_id, total_amount, order_status, sys_cret_dt)
VALUES (600001, 10, 25000000, 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_item (order_id, product_id, quantity, total_amount, sys_cret_dt)
VALUES (600001, 900002, 25000, 25000000, DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_address (order_id, receiver_name, phone, address1, address2, zipcode, memo, sys_cret_dt)
VALUES (600001, '조회용수령인', '01011112222', '서울시 테스트구', '101동', '12345', '문 앞', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_dt, sys_cret_dt)
VALUES (600001, 25000000, 'BALANCE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_history (id, order_id, status, sys_cret_dt)
VALUES (500001, 600001, 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO product (id, seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt, sys_cret_dt)
VALUES (900003, 1, '주문취소동시성테스트상품', 10000, 10, 'TENT', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));
INSERT INTO orders (id, user_id, total_amount, order_status, sys_cret_dt)
VALUES (600003, 100, 10000, 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_item (order_id, product_id, quantity, total_amount, sys_cret_dt)
VALUES (600003, 900003, 1, 10000, DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_address (order_id, receiver_name, phone, address1, address2, zipcode, memo, sys_cret_dt)
VALUES (600003, '조회용수령인', '01033334444', '서울시 테스트구', '101동', '12345', '문 앞', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_dt, sys_cret_dt)
VALUES (600003, 10000, 'BALANCE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_history (id, order_id, status, sys_cret_dt)
VALUES (500003, 600003, 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO orders (id, user_id, total_amount, order_status, sys_cret_dt)
VALUES (600004, 200, 10000, 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_item (order_id, product_id, quantity, total_amount, sys_cret_dt)
VALUES (600004, 900003, 1, 10000, DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_address (order_id, receiver_name, phone, address1, address2, zipcode, memo, sys_cret_dt)
VALUES (600004, '조회용수령인2', '01044445555', '서울시 테스트구', '201동', '62345', '문 앞', DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO payment (order_id, amount, payment_method, payment_status, paid_dt, sys_cret_dt)
VALUES (600004, 10000, 'BALANCE', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));
INSERT INTO order_history (id, order_id, status, sys_cret_dt)
VALUES (500004, 600004, 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO product (id, seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt, sys_cret_dt)
VALUES (900004, 1, '주문동시성테스트상품', 10000, 10, 'TENT', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY));