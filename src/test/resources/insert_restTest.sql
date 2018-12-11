INSERT INTO users (id, user_name, user_password, user_state)
VALUES (1, 'Admin', '$2a$10$m0puVcm.XzTvSQ31uajEYumc73fIDaGJG3/RTFPpDMZ3BQYtmMnrG', 0);
INSERT INTO users (id, user_name, user_password, user_state)
VALUES (2, 'John', '$2a$10$gPCFfSC7vYvN1.NOIyI7deMU1QOL6kPICmzKU4O75EJK0Akr.nBEe', 1);
INSERT INTO users (id, user_name, user_password, user_state)
VALUES (3, 'Bob', '$2a$10$gPCFfSC7vYvN1.NOIyI7deMU1QOL6kPICmzKU4O75EJK0Akr.nBEe', 1);
INSERT INTO currencies (id, currency_code, currency_name) VALUES (1, 'USD', 'Американський долар');
INSERT INTO currencies (id, currency_code, currency_name) VALUES (2, 'EUR', 'Євро');
INSERT INTO currencies (id, currency_code, currency_name) VALUES (3, 'RUB', 'Російський рубль');
INSERT INTO rates (id, rate_currency_id, rate_date, rate_sale, rate_buy)
    VALUES (1,1,'2018-12-06 08:00:00.000',28.15,27.85);
INSERT INTO rates (id, rate_currency_id, rate_date, rate_sale, rate_buy)
    VALUES (2,2,'2018-12-06 08:00:00.00',32.00,30.50);
INSERT INTO rates (id, rate_currency_id, rate_date, rate_sale, rate_buy)
    VALUES (3,3,'2018-12-06 08:00:00.00',0.45,0.40);
INSERT INTO nbu_rates (id, nbu_rate_currency_id, nbu_rate, nbu_rate_date)
    VALUES (1,1,28.00,'2018-12-06 00:00:00.000');
INSERT INTO nbu_rates (id, nbu_rate_currency_id, nbu_rate, nbu_rate_date)
    VALUES (2,2,30.75,'2018-12-06 00:00:00.000');
INSERT INTO nbu_rates (id, nbu_rate_currency_id, nbu_rate, nbu_rate_date)
    VALUES (3,3,0.43,'2018-12-06 00:00:00.000');
INSERT INTO operations (id, operation_rate_id, operation_nbu_rate_id, operation_user_id,
    operation_buy, operation_sum_hrn, operation_sum_curr, operation_time, operation_deleted)
    VALUES (1,1,1,2,true,2815.0,100.0,'2018-12-06 13:05:00.000', false);
INSERT INTO operations (id, operation_rate_id, operation_nbu_rate_id, operation_user_id,
    operation_buy, operation_sum_hrn, operation_sum_curr, operation_time, operation_deleted)
    VALUES (2,2,2,2,false,3050.0,100.0,'2018-12-06 13:10:00.000', false);