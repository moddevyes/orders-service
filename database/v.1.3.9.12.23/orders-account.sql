use ecommerce_orders_db;

ALTER TABLE orders_account add column account_ref_id VARCHAR(255)            NOT NULL;

CREATE TABLE orders_account
(
    id             BIGINT AUTO_INCREMENT   NOT NULL,
    account_ref_id VARCHAR(255)            NOT NULL,
    first_name     VARCHAR(200) DEFAULT '' NOT NULL,
    last_name      VARCHAR(200) DEFAULT '' NOT NULL,
    email_address  VARCHAR(200) DEFAULT '' NOT NULL,
    created_dt     datetime                NULL,
    updated_dt     datetime                NULL,
    CONSTRAINT pk_orders_account PRIMARY KEY (id)
);

CREATE TABLE orders_account_addresses
(
    orders_account_id BIGINT NOT NULL,
    addresses_id      BIGINT NOT NULL,
    CONSTRAINT pk_orders_account_addresses PRIMARY KEY (orders_account_id, addresses_id)
);

ALTER TABLE orders_account
    ADD CONSTRAINT uc_orders_account_accountrefid UNIQUE (account_ref_id);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT uc_orders_account_addresses_addresses UNIQUE (addresses_id);

ALTER TABLE orders_account
    ADD CONSTRAINT uc_orders_account_emailaddress UNIQUE (email_address);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT fk_ordaccadd_on_orders_account FOREIGN KEY (orders_account_id) REFERENCES orders_account (id);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT fk_ordaccadd_on_orders_address FOREIGN KEY (addresses_id) REFERENCES orders_address (id);