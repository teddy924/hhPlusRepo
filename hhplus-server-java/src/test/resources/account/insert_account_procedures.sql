CREATE PROCEDURE insert_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000 DO
        INSERT INTO users (email, password, name, phone)
        VALUES (
            CONCAT('user', i, '@test.com'),
            'pw1234',
            CONCAT('User_', i),
            CONCAT('0101234', LPAD(i, 4, '0'))
        );
        SET i = i + 1;
    END WHILE;
END;

CREATE PROCEDURE insert_accounts()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000 DO
        INSERT INTO account (user_id, balance)
        VALUES (
            i,
            100000
        );
        SET i = i + 1;
    END WHILE;
END;

CREATE PROCEDURE insert_account_histories()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 1000 DO
        -- 각 사용자에 대해 3개의 랜덤 이력을 생성
        INSERT INTO account_history (account_id, status, amount, sys_cret_dt)
        VALUES
            (i, 'CHARGE', 1000 + FLOOR(RAND() * 5000), NOW()),
            (i, 'USE', 500 + FLOOR(RAND() * 2000), NOW()),
            (i, 'CHARGE', 2000 + FLOOR(RAND() * 3000), NOW());

        SET i = i + 1;
END WHILE;
END;