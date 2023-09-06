
CREATE TABLE orders_address
(
    id                  BIGINT AUTO_INCREMENT   NOT NULL,
    address1            VARCHAR(200) DEFAULT '' NOT NULL,
    address2            VARCHAR(200) DEFAULT '' NULL,
    city                VARCHAR(200) DEFAULT '' NOT NULL,
    state               VARCHAR(2)   DEFAULT '' NOT NULL,
    province            VARCHAR(200)            null DEFAULT '' NULL,
    postal_code         VARCHAR(10)  DEFAULT '' NOT NULL,
    country             VARCHAR(100) DEFAULT '' NOT NULL,
    is_shipping_address BIT(1)                  NOT NULL,
    created_dt          datetime                NULL,
    updated_dt          datetime                NULL,
    CONSTRAINT pk_orders_address PRIMARY KEY (id)
);

CREATE TABLE orders_account
(
    id            BIGINT AUTO_INCREMENT   NOT NULL,
    first_name    VARCHAR(200) DEFAULT '' NOT NULL,
    last_name     VARCHAR(200) DEFAULT '' NOT NULL,
    email_address VARCHAR(200) DEFAULT '' NOT NULL,
    created_dt    datetime                NULL,
    updated_dt    datetime                NULL,
    CONSTRAINT pk_orders_account PRIMARY KEY (id)
);

CREATE TABLE orders_account_addresses
(
    orders_account_id BIGINT NOT NULL,
    addresses_id      BIGINT NOT NULL,
    CONSTRAINT pk_orders_account_addresses PRIMARY KEY (orders_account_id, addresses_id)
);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT uc_orders_account_addresses_addresses UNIQUE (addresses_id);

ALTER TABLE orders_account
    ADD CONSTRAINT uc_orders_account_emailaddress UNIQUE (email_address);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT fk_ordaccadd_on_orders_account FOREIGN KEY (orders_account_id) REFERENCES orders_account (id);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT fk_ordaccadd_on_orders_address FOREIGN KEY (addresses_id) REFERENCES orders_address (id);