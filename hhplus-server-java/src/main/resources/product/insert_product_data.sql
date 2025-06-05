INSERT INTO product (
    seller_id, name, price, stock, category, efct_st_dt, efct_fns_dt
)
SELECT
    FLOOR(1 + RAND() * 10),               -- seller_id (1~10)
    CONCAT('Product_', id),               -- name
    10000 + (id * 100),                   -- price
    1000,                                 -- stock
    ELT(
            CASE
                WHEN id <= 10 THEN 1
                WHEN id <= 40 THEN 2
                WHEN id <= 55 THEN 3
                WHEN id <= 75 THEN 4
                WHEN id <= 85 THEN 5
                ELSE 6
                END,
            'TENT', 'TARP', 'FURNITURE', 'BEDDING', 'KITCHENWARE', 'ACC'
    ) AS category,                        -- category
    DATE_SUB(NOW(), INTERVAL 30 DAY),     -- efct_st_dt
    DATE_ADD(NOW(), INTERVAL 30 DAY)      -- efct_fns_dt
FROM (
         SELECT @rownum_p := @rownum_p + 1 AS id
         FROM information_schema.tables t1,
             (SELECT @rownum_p := 0) r
             LIMIT 105
     ) tmp;
