CREATE TABLE IF NOT EXISTS users(
id              INT UNSIGNED    NOT NULL AUTO_INCREMENT,
user_name       VARCHAR(20)     NOT NULL UNIQUE,
user_password   VARCHAR(70)     NOT NULL,
user_state      SMALLINT        NOT NULL,
CONSTRAINT pkey_user PRIMARY KEY (id),
CONSTRAINT user_state_check CHECK (user_state >= 0 AND user_state <= 1));

CREATE TABLE IF NOT EXISTS currencies(
id              INT UNSIGNED    NOT NULL AUTO_INCREMENT,
currency_code   VARCHAR(20)     NOT NULL UNIQUE,
currency_name   VARCHAR(70)     NOT NULL UNIQUE,
CONSTRAINT pkey_currencies PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS rates(
id                  INT UNSIGNED    NOT NULL AUTO_INCREMENT,
rate_currency_id    INT UNSIGNED    NOT NULL,
rate_date           TIMESTAMP       NOT NULL,
rate_sale           FLOAT           NOT NULL,
rate_buy            FLOAT           NOT NULL,
CONSTRAINT pkey_rate PRIMARY KEY (id),
CONSTRAINT rate_sale_check CHECK (rate_sale >= 0.0),
CONSTRAINT rate_buy_check CHECK (rate_buy >= 0.0),
CONSTRAINT fkey_currency_to_rate FOREIGN KEY (rate_currency_id)
    REFERENCES currencies (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS nbu_rates(
id                      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
nbu_rate_currency_id    INT UNSIGNED    NOT NULL,
nbu_rate                FLOAT           NOT NULL,
nbu_rate_date           TIMESTAMP       NOT NULL,
CONSTRAINT pkey_nbu_rate PRIMARY KEY (id),
CONSTRAINT nbu_rate_check CHECK (nbu_rate >= 0.0),
CONSTRAINT fkey_currency_to_nbu_rate FOREIGN KEY (nbu_rate_currency_id)
    REFERENCES currencies (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS operations(
id                      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
operation_rate_id       INT UNSIGNED    NOT NULL,
operation_nbu_rate_id   INT UNSIGNED    NOT NULL,
operation_user_id       INT UNSIGNED    NOT NULL,
operation_buy           BOOLEAN         NOT NULL,
operation_sum_hrn       FLOAT           NOT NULL,
operation_sum_curr      FLOAT           NOT NULL,
operation_time          TIMESTAMP       NOT NULL,
operation_deleted       BOOLEAN         NOT NULL,
CONSTRAINT pkey_operation PRIMARY KEY (id),
CONSTRAINT sum_hrn_check CHECK (operation_sum_hrn >= 0.0),
CONSTRAINT sum_curr_check CHECK (operation_sum_curr >= 0.0),
CONSTRAINT fkey_rate_to_operation FOREIGN KEY (operation_rate_id)
    REFERENCES rates (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fkey_nbu_rate_to_operation FOREIGN KEY (operation_nbu_rate_id)
    REFERENCES nbu_rates (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT fkey_user_to_operation FOREIGN KEY (operation_user_id)
    REFERENCES users (id)
    ON DELETE NO ACTION ON UPDATE NO ACTION);
