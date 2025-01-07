-- Wrapping 데이터 생성 (5~10개)

INSERT INTO wrapping (name, price)
SELECT CONCAT('Wrapping ', seq), FLOOR(RAND() * 5000 + 1000)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
LIMIT 5;
-- OrderGroup 데이터 생성 (20개)
INSERT INTO order_group (address, delivery_price, ordered_at, ordered_name, recipient_home_phone, recipient_name, recipient_phone, user_id, wrapping_id)
SELECT
    CONCAT('Address ', seq),
    FLOOR(RAND() * 3000 + 500),
    CURDATE() - INTERVAL FLOOR(RAND() * 30) DAY,
    CONCAT('Orderer ', seq),
    IF(FLOOR(RAND() * 2) = 1, CONCAT('010-', FLOOR(RAND() * 9000 + 1000), '-', FLOOR(RAND() * 9000 + 1000)), NULL),
    CONCAT('Recipient ', seq),
    CONCAT('010-', FLOOR(RAND() * 9000 + 1000), '-', FLOOR(RAND() * 9000 + 1000)),
    4,
    (SELECT id FROM wrapping ORDER BY RAND() LIMIT 1)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20) t;

-- OrderDetail 데이터 생성 (1~3개 per OrderGroup)
INSERT INTO order_detail (amount, book_id, discount_price, order_status, prime_price, order_group_id)
SELECT
    FLOOR(RAND() * 5 + 1),
    FLOOR(RAND() * 50 + 1),
    FLOOR(RAND() * 500 + 500),
    CASE FLOOR(RAND() * 8 + 1)
        WHEN 1 THEN 'DELIVERED'
        WHEN 2 THEN 'ERROR'
        WHEN 3 THEN 'ORDER_CANCELED'
        WHEN 4 THEN 'PAYMENT_COMPLETED'
        WHEN 5 THEN 'PAYMENT_PENDING'
        WHEN 6 THEN 'RETURNED'
        WHEN 7 THEN 'RETURNED_PENDING'
        WHEN 8 THEN 'SHIPPING'
        END,
    FLOOR(RAND() * 10000 + 2000),
    g.id
FROM order_group g
         CROSS JOIN (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3) t
WHERE RAND() < 0.5;

-- DeliveryInfo 데이터 생성
INSERT INTO delivery_info (order_group_id, arrived_at, invoice_number, name, shipping_at)
SELECT
    g.id,
    IF(FLOOR(RAND() * 2) = 1, CURDATE() - INTERVAL FLOOR(RAND() * 10) DAY, NULL),
    FLOOR(RAND() * 900000 + 100000),
    CONCAT('Delivery ', FLOOR(RAND() * 100)),
    IF(FLOOR(RAND() * 2) = 1, CURDATE() - INTERVAL FLOOR(RAND() * 20) DAY, NULL)
FROM order_group g;

-- DeliveryCode 데이터 생성 (5~10개)
INSERT INTO delivery_code (id, international, name)
SELECT
    CONCAT('DC', seq),
    FLOOR(RAND() * 2),
    CONCAT('Delivery Code ', seq)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
    LIMIT 30;

-- DeliveryPolicy 데이터 생성 (5~10개)
INSERT INTO delivery_policy (name, price)
SELECT CONCAT('Policy ', seq), FLOOR(RAND() * 5000 + 500)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
    LIMIT  5;

-- PayType 데이터 생성 (5~10개)
INSERT INTO pay_type (name)
SELECT CONCAT('Pay Type ', seq)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
    LIMIT 5;

-- PointPolicy 데이터 생성 (5~10개)
INSERT INTO point_policy (amount, name, rate)
SELECT FLOOR(RAND() * 1000 + 100), CONCAT('Point Policy ', seq), 0
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
    LIMIT  10;

INSERT INTO point_policy (amount, name, rate)
SELECT 0, CONCAT('Point Policy ', seq), ROUND(RAND() * 10, 2)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
LIMIT  10;

-- Pay 데이터 생성
INSERT INTO pay (order_id, payment_key, price, requested_at, status, order_group_id, pay_type_id)
SELECT
    FLOOR(RAND() * 1000 + 1),
    CONCAT('PAY', FLOOR(RAND() * 1000000 + 1000)),
    FLOOR(RAND() * 50000 + 5000),
    CURDATE() - INTERVAL FLOOR(RAND() * 15) DAY,
    CASE FLOOR(RAND() * 2) WHEN 0 THEN 'SUCCESS' ELSE 'FAILED' END,
    g.id,
    (SELECT id FROM pay_type ORDER BY RAND() LIMIT 1)
FROM order_group g;

-- PointHistory 데이터 생성
INSERT INTO point_history (amount, changed_at, comment, member_id, types, order_group_id)
SELECT
    FLOOR(RAND() * 500 + 100),
    NOW() - INTERVAL FLOOR(RAND() * 100) DAY,
    CONCAT('Comment ', FLOOR(RAND() * 100)),
    FLOOR(RAND() * 1000 + 1),
    CASE FLOOR(RAND() * 2) WHEN 0 THEN 'EARN' ELSE 'SPEND' END,
    g.id
FROM order_group g;