INSERT INTO point_policy (name, amount, rate) VALUES ('Welcome Bonus', 100, 0);
INSERT INTO point_policy (name, amount, rate) VALUES ('Referral Bonus', 200, 0.);
INSERT INTO point_policy (name, amount, rate) VALUES ('Loyalty Reward', 500, 0);
INSERT INTO point_policy (name, amount, rate) VALUES ('Seasonal Bonus', 0, 0.15);
INSERT INTO point_policy (name, amount, rate) VALUES ('Anniversary Bonus', 0, 0.3);



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


