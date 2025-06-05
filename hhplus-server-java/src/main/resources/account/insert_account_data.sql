INSERT INTO users (id, email, password, name, phone)
SELECT
    id,
    CONCAT('user', id, '@test.com'),
    'pw1234',
    CONCAT('User_', id),
    CONCAT('0101234', LPAD(id, 4, '0'))
FROM (
    SELECT @rownum_u := @rownum_u + 1 AS id
    FROM information_schema.tables t1,
         information_schema.tables t2,
         (SELECT @rownum_u := 0) r
         LIMIT 1000
) tmp;


INSERT INTO account (id, user_id, balance)
SELECT
    id,
    id,         -- user_id와 동일하게 매핑 (1:1)
    100000
FROM (
     SELECT @rownum_a := @rownum_a + 1 AS id
     FROM information_schema.tables t1,
         information_schema.tables t2,
         (SELECT @rownum_a := 0) r
         LIMIT 1000
) tmp;


INSERT INTO account_history (account_id, status, amount, sys_cret_dt)
SELECT
    id,
    'CHARGE',
    1000 + FLOOR(RAND() * 5000),
    NOW()
FROM (
         SELECT @rownum_h1 := @rownum_h1 + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             (SELECT @rownum_h1 := 0) r
             LIMIT 1000
     ) tmp
UNION ALL
SELECT
    id,
    'USE',
    500 + FLOOR(RAND() * 2000),
    NOW()
FROM (
         SELECT @rownum_h2 := @rownum_h2 + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             (SELECT @rownum_h2 := 0) r
             LIMIT 1000
     ) tmp
UNION ALL
SELECT
    id,
    'CHARGE',
    2000 + FLOOR(RAND() * 3000),
    NOW()
FROM (
         SELECT @rownum_h3 := @rownum_h3 + 1 AS id
         FROM information_schema.tables t1,
             information_schema.tables t2,
             (SELECT @rownum_h3 := 0) r
             LIMIT 1000
) tmp;
