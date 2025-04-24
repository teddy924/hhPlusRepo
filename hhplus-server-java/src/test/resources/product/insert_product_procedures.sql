CREATE PROCEDURE insert_products()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 105 DO
        INSERT INTO product (
            seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt
        )
        VALUES (
            FLOOR(1 + RAND() * 10),
            CONCAT('Product_', i),
            10000 + (i * 100),
            1000,
            ELT(
                CASE
                    WHEN i <= 10 THEN 1
                    WHEN i <= 40 THEN 2
                    WHEN i <= 55 THEN 3
                    WHEN i <= 75 THEN 4
                    WHEN i <= 85 THEN 5
                    ELSE 6
                END,
                'TENT', 'TARP', 'FURNITURE', 'BEDDING', 'KITCHENWARE', 'ACC'
            ),
            DATE_SUB(NOW(), INTERVAL 30 DAY),
            DATE_ADD(NOW(), INTERVAL 30 DAY)
        );
        SET i = i + 1;
    END WHILE;
END;