INSERT INTO users (id, user_name, user_password, user_state) VALUES (1, 'admin', 'password', 0);
INSERT INTO users (id, user_name, user_password, user_state) VALUES (2, 'first operator', 'password', 1);
INSERT INTO users (id, user_name, user_password, user_state) VALUES (3, 'second operator', 'password', 1);
INSERT INTO currencies (id, currency_code, currency_name) VALUES (1, 'USD', 'American dollar');
INSERT INTO currencies (id, currency_code, currency_name) VALUES (2, 'EUR', 'Euro');
INSERT INTO currencies (id, currency_code, currency_name) VALUES (3, 'GBP', 'Great Britain pound');
INSERT INTO currencies (id, currency_code, currency_name) VALUES (4, 'UAH', 'Ukrainian hryvnia');
INSERT INTO rates (id, rate_currency_id, rate_date, rate_sale, rate_buy)
    VALUES (1,1,'2018-09-02 08:00:00.000',28.15,27.85);
INSERT INTO rates (id, rate_currency_id, rate_date, rate_sale, rate_buy)
    VALUES (2,2,'2018-09-02 08:00:00.00',30.5,32.00);
INSERT INTO rates (id, rate_currency_id, rate_date, rate_sale, rate_buy)
    VALUES (3,2,'2018-09-03 08:00:00.00',30.55,32.05);
INSERT INTO nbu_rates (id, nbu_rate_currency_id, nbu_rate, nbu_rate_date)
    VALUES (1,1,28.00,'2018-09-02 00:00:00.000');
INSERT INTO nbu_rates (id, nbu_rate_currency_id, nbu_rate, nbu_rate_date)
    VALUES (2,2,30.75,'2018-09-02 00:00:00.000');
INSERT INTO nbu_rates (id, nbu_rate_currency_id, nbu_rate, nbu_rate_date)
    VALUES (3,2,30.70,'2018-09-03 00:00:00.000');
INSERT INTO operations (id, operation_rate_id, operation_nbu_rate_id, operation_user_id,
    operation_buy, operation_sum_hrn, operation_sum_curr, operation_time, operation_deleted)
    VALUES (1,1,1,2,true,2815.0,100.0,'2018-09-02 13:05:00.000', false);
INSERT INTO operations (id, operation_rate_id, operation_nbu_rate_id, operation_user_id,
    operation_buy, operation_sum_hrn, operation_sum_curr, operation_time, operation_deleted)
    VALUES (2,2,2,2,false,3050.0,100.0,'2018-09-02 13:10:00.000', false);