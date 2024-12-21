INSERT INTO point_policy (name, amount, rate) VALUES ('Welcome Bonus', 5000, 0);
INSERT INTO point_policy (name, amount, rate) VALUES ('Review Bonus', 500, 0);
INSERT INTO point_policy (name, amount ,rate) VALUES ( 'Payment Point',0,1.5 );



-- 포인트 기록 데이터

-- Initial deposit bonus
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('EARN', 150, '2024-12-08 09:00:00', 'Initial deposit bonus', 1);

-- Points spent on gift
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('SPEND', -30, '2024-12-08 18:30:00', 'Points spent on gift', 1);

-- Anniversary bonus
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('EARN', 500, '2024-12-07 15:20:00', 'Anniversary bonus', 2);

-- Discount on purchase
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('SPEND', -100, '2024-12-06 20:45:00', 'Discount on purchase', 2);

-- Monthly subscription bonus
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('EARN', 300, '2024-12-11 10:15:00', 'Monthly subscription bonus', 3);

-- Referral bonus
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('EARN', 250, '2024-12-10 13:00:00', 'Referral bonus', 4);

-- Points redeemed for service
INSERT INTO point_history (types, amount, changed_at, comment, member_id)
VALUES ('SPEND', -70, '2024-12-09 08:50:00', 'Points redeemed for service', 4);


-- Insert PayType with name "Credit Card"
INSERT INTO pay_types (name) VALUES ('Credit Card');

-- Insert PayType with name "PayPal"
INSERT INTO pay_types (name) VALUES ('PayPal');

-- Insert PayType with name "Bank Transfer"

-- Insert PayType with name "Cash"
INSERT INTO pay_types (name) VALUES ('Cash');

-- Insert PayType with name "Mobile Payment"
INSERT INTO pay_types (name) VALUES ('Mobile Payment');

-- Wrapping 테이블 데이터 삽입
INSERT INTO wrapping (name, price) VALUES ('Gift Wrap', 200);
INSERT INTO wrapping (name, price) VALUES ('Standard Wrap', 100);

-- DeliveryPolicy 테이블 데이터 삽입
INSERT INTO delivery_policy (name, price) VALUES ('Standard Delivery', 3000);
INSERT INTO delivery_policy (name, price) VALUES ('Express Delivery', 5000);

-- DeliveryInfo 테이블 데이터 삽입
INSERT INTO delivery_info (name, invoice_number, forwarded_at, delivery_date, arrived_at)
VALUES ('John Doe', 12345, '2024-12-10 15:00:00', '2024-12-12', '2024-12-14 10:00:00');
INSERT INTO delivery_info (name, invoice_number, forwarded_at, delivery_date, arrived_at)
VALUES ('Jane Smith', 12346, '2024-12-11 10:30:00', '2024-12-13', '2024-12-15 12:00:00');

-- OrderGroup 테이블 데이터 삽입
INSERT INTO order_group (user_id, ordered_name, ordered_at, recipient_name, recipient_phone, delivery_price, wrapping_id, delivery_info_id)
VALUES (1, 'Order 1', '2024-12-08 14:30:00', 'John Doe', '010-1234-5678', 3000, 1, 1);
INSERT INTO order_group (user_id, ordered_name, ordered_at, recipient_name, recipient_phone, delivery_price, wrapping_id, delivery_info_id)
VALUES (2, 'Order 2', '2024-12-09 11:00:00', 'Jane Smith', '010-2345-6789', 5000, 2, 2);

-- OrderDetail 테이블 데이터 삽입
INSERT INTO order_detail (book_id, amount, status, price, wrapping_id, order_group_id)
VALUES (1001, 2, 'PAYMENT_PENDING', 20000, 1, 1);
INSERT INTO order_detail (book_id, amount, status, price, wrapping_id, order_group_id)
VALUES (1002, 1, 'PAYMENT_PENDING', 15000, 2, 2);

-- DeliveryInfo 업데이트 (필요시)
UPDATE order_group SET delivery_info_id = 1 WHERE id = 1;
UPDATE order_group SET delivery_info_id = 2 WHERE id = 2;