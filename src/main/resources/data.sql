INSERT INTO point_policy (name, amount, rate) VALUES ('Welcome Bonus', 100, 0.1);
INSERT INTO point_policy (name, amount, rate) VALUES ('Referral Bonus', 200, 0.15);
INSERT INTO point_policy (name, amount, rate) VALUES ('Loyalty Reward', 500, 0.2);
INSERT INTO point_policy (name, amount, rate) VALUES ('Seasonal Bonus', 300, 0.12);
INSERT INTO point_policy (name, amount, rate) VALUES ('Anniversary Bonus', 400, 0.18);

-- 멤버 데이터
INSERT INTO member (id) VALUES (1);
INSERT INTO member (id) VALUES (2);
INSERT INTO member (id) VALUES (3);
INSERT INTO member (id) VALUES (4);
INSERT INTO member (id) VALUES (5);

-- 포인트 기록 데이터

-- 1. Initial deposit bonus
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (1, 'EARN', 150, '2024-12-08 09:00:00', 'Initial deposit bonus', 1);

-- 2. Points spent on gift
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (2, 'SPEND', -30, '2024-12-08 18:30:00', 'Points spent on gift', 1);

-- 3. Anniversary bonus
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (3, 'EARN', 500, '2024-12-07 15:20:00', 'Anniversary bonus', 2);

-- 4. Discount on purchase
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (4, 'SPEND', -100, '2024-12-06 20:45:00', 'Discount on purchase', 2);

-- 5. Monthly subscription bonus
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (5, 'EARN', 300, '2024-12-11 10:15:00', 'Monthly subscription bonus', 3);

-- 6. Referral bonus
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (6, 'EARN', 250, '2024-12-10 13:00:00', 'Referral bonus', 4);

-- 7. Points redeemed for service
INSERT INTO point_history (id, types, amount, changed_at, comment, member_id)
VALUES (7, 'SPEND', -70, '2024-12-09 08:50:00', 'Points redeemed for service', 4);


