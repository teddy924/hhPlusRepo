
DROP TABLE IF EXISTS coupon_issue;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS order_coupon;
DROP TABLE IF EXISTS order_history;
DROP TABLE IF EXISTS order_address;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS account_history;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    balance BIGINT NOT NULL DEFAULT 0,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE account_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    status ENUM('CHARGE', 'USE', 'REFUND') NOT NULL,
    amount BIGINT NOT NULL CHECK (amount > 0),
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price BIGINT NOT NULL,
    stock INT NOT NULL,
    category ENUM('TENT', 'TARP', 'FURNITURE', 'BEDDING', 'KITCHENWARE', 'ACC') NOT NULL,
    efct_st_dt TIMESTAMP NOT NULL,
    efct_fns_dt TIMESTAMP NOT NULL,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount BIGINT NOT NULL,
    order_status ENUM('CREATED', 'PAID', 'CANCELED') NOT NULL,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    total_amount BIGINT NOT NULL,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address1 VARCHAR(255) NOT NULL,
    address2 VARCHAR(255) NOT NULL,
    zipcode VARCHAR(10) NOT NULL,
    memo VARCHAR(255),
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE order_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    status ENUM('CREATED', 'PAID', 'CANCELED') NOT NULL,
    memo VARCHAR(255),
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    coupon_issue_id BIGINT NOT NULL,
    discount_amount BIGINT NOT NULL,
    used_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    payment_method ENUM('CARD', 'CASH', 'BALANCE') NOT NULL,
    payment_status ENUM('COMPLETED', 'CANCELLED') NOT NULL,
    paid_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    discount_type ENUM('AMOUNT', 'RATE') NOT NULL,
    discount_value BIGINT NOT NULL,
    limit_quantity INT NOT NULL,
    remain_quantity INT NOT NULL,
    efct_st_dt TIMESTAMP NOT NULL,
    efct_fns_dt TIMESTAMP NOT NULL,
    sys_cret_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sys_chg_dt TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE coupon_issue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status ENUM('ISSUED', 'USED', 'EXPIRED') NOT NULL,
    issued_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_dt TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_email ON users(email);

CREATE UNIQUE INDEX idx_account_user_id ON account(user_id);

CREATE INDEX idx_account_history_account_id ON account_history(account_id);
CREATE INDEX idx_account_history_sys_cret_dt ON account_history(sys_cret_dt);

CREATE INDEX idx_product_seller_id ON product(seller_id);
CREATE INDEX idx_product_category ON product(category);
CREATE INDEX idx_product_efct_st_fns_dt ON product(efct_st_dt, efct_fns_dt);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_sys_cret_dt ON orders(sys_cret_dt);

CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_order_item_product_id ON order_item(product_id);

CREATE INDEX idx_order_address_order_id ON order_address(order_id);

CREATE INDEX idx_order_history_order_id ON order_history(order_id);
CREATE INDEX idx_order_history_status ON order_history(status);

CREATE INDEX idx_order_coupon_order_id ON order_coupon(order_id);
CREATE INDEX idx_order_coupon_coupon_issue_id ON order_coupon(coupon_issue_id);

CREATE INDEX idx_payment_order_id ON payment(order_id);
CREATE INDEX idx_payment_status ON payment(payment_status);

CREATE INDEX idx_coupon_issue_user_coupon ON coupon_issue(user_id, coupon_id);
CREATE INDEX idx_coupon_issue_coupon_id ON coupon_issue(coupon_id);
CREATE INDEX idx_coupon_issue_user_id ON coupon_issue(user_id);

CREATE INDEX idx_coupon_efct_st_fns_dt ON coupon(efct_st_dt, efct_fns_dt);
