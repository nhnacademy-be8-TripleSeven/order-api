-- Wrapping 데이터 생성 (5~10개)
INSERT INTO wrapping (name, price)
SELECT CONCAT('Wrapping ', seq), FLOOR(RAND() * 5000 + 1000)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
LIMIT 5;

-- DeliveryPolicy 데이터 생성 (5~10개)
INSERT INTO delivery_policy (name, price, min_price)
SELECT CONCAT('Policy ', seq), FLOOR(RAND() * 5000 + 500), FLOOR(RAND() * 3000 + 100)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
LIMIT 5;

-- DefaultDeliveryPolicy 데이터 생성 (3개)
INSERT INTO default_delivery_policy (delivery_policy_type, delivery_policy_id)
SELECT
    CASE seq
        WHEN 1 THEN 'DEFAULT'
        WHEN 2 THEN 'ERROR'
        WHEN 3 THEN 'EVENT'
        END,
    dp.id
FROM delivery_policy dp
         CROSS JOIN (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3) t
LIMIT 3;

-- PayType 데이터 생성 (5~10개)
INSERT INTO pay_type (name) VALUES
                                ('토스페이'),
                                ('네이버페이'),
                                ('카카오페이'),
                                ('신용카드'),
                                ('계좌 이체'),
                                ('무통장 입금');

-- PointPolicy 데이터 생성 (5~10개)
INSERT INTO point_policy (amount, name, rate)
SELECT FLOOR(RAND() * 1000 + 100), CONCAT('Point Policy ', seq), ROUND(RAND() * 10, 2)
FROM (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t
LIMIT 5;

-- DefaultPointPolicy 데이터 생성 (3개)
INSERT INTO default_point_policy (point_policy_type, point_policy_id)
SELECT
    CASE seq
        WHEN 1 THEN 'CONTENT_REVIEW'
        WHEN 2 THEN 'DEFAULT_BUY'
        WHEN 3 THEN 'REGISTER'
        END,
    pp.id
FROM point_policy pp
         CROSS JOIN (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3) t
LIMIT 3;

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
    1,
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
    CURDATE() - INTERVAL FLOOR(RAND() * 10) DAY, -- 도착 날짜 (랜덤 생성)
    FLOOR(RAND() * 900000 + 100000),            -- 송장 번호
    CONCAT('Delivery ', FLOOR(RAND() * 100)),   -- 배송 이름
    CURDATE() - INTERVAL FLOOR(RAND() * 20) DAY -- 발송 날짜 (랜덤 생성)
FROM order_group g;

-- PointHistory 데이터 생성 (50개, 사용과 적립 분리)
INSERT INTO point_history (amount, changed_at, comment, member_id, types)
SELECT
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN -1 * FLOOR(RAND() * 500 + 100)  -- 사용 (음수)
        ELSE FLOOR(RAND() * 500 + 100)             -- 적립 (양수)
        END AS amount,
    NOW() - INTERVAL FLOOR(RAND() * 100) DAY,       -- 최근 100일 내 변경 시간
    CONCAT('Comment ', FLOOR(RAND() * 100)),        -- 랜덤 코멘트
    1,                                              -- member_id 예시
    CASE FLOOR(RAND() * 2)
        WHEN 0 THEN 'SPEND'                         -- 사용
        ELSE 'EARN'                                 -- 적립
        END AS types
FROM (SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
      UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) t
LIMIT 50;

-- OrderGroupPointHistory 데이터 생성
INSERT INTO order_group_point_history (order_group_id, point_history_id)
SELECT
    og.id,
    ph.id
FROM order_group og
         JOIN point_history ph ON ph.member_id IS NOT NULL
WHERE ph.types IN ('EARN', 'SPEND')
LIMIT 30;

-- Pay 데이터 생성
INSERT INTO pay (order_id, payment_key, price, requested_at, status, order_group_id, pay_type_id)
SELECT
    FLOOR(RAND() * 1000 + 1),
    CONCAT('PAY', FLOOR(RAND() * 1000000 + 1000)),
    FLOOR(RAND() * 50000 + 5000),
    CURDATE() - INTERVAL FLOOR(RAND() * 15) DAY,
    CASE FLOOR(RAND() * 2) WHEN 0 THEN 'READY' ELSE 'DONE' END,
    g.id,
    (SELECT id FROM pay_type ORDER BY RAND() LIMIT 1)
FROM order_group g;