USE ecommerce_orders_db;

-- ORDERS ADDRESS
CREATE TABLE orders_address
(
    id          BIGINT AUTO_INCREMENT   NOT NULL,
    address1    VARCHAR(200) DEFAULT '' NOT NULL,
    address2    VARCHAR(200) DEFAULT '' NULL,
    city        VARCHAR(200) DEFAULT '' NOT NULL,
    state       VARCHAR(2)   DEFAULT '' NOT NULL,
    province    VARCHAR(200)            null DEFAULT '' NULL,
    postal_code VARCHAR(10)  DEFAULT '' NOT NULL,
    country     VARCHAR(100) DEFAULT '' NOT NULL,
    CONSTRAINT pk_orders_address PRIMARY KEY (id)
);

CREATE TABLE orders_address_orders
(
    orders_address_id BIGINT NOT NULL,
    orders_id         BIGINT NULL,
    CONSTRAINT pk_orders_address_orders PRIMARY KEY (orders_address_id)
);


-- ORDERS ACCOUNT
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

CREATE TABLE orders_account_orders
(
    orders_account_id BIGINT NOT NULL,
    orders_id         BIGINT NULL,
    CONSTRAINT pk_orders_account_orders PRIMARY KEY (orders_account_id)
);


-- ORDER LINE ITEMS
CREATE TABLE order_line_items
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    order_id    BIGINT                NOT NULL,
    product_id  BIGINT                NOT NULL,
    shipment_id BIGINT                NULL,
    quantity    INT                   NOT NULL,
    price       DECIMAL               NOT NULL,
    total_price DECIMAL               NULL,
    created_dt  datetime              NULL,
    updated_dt  datetime              NULL,
    CONSTRAINT pk_order_line_items PRIMARY KEY (id)
);

-- ORDERS
CREATE TABLE orders
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    order_number VARCHAR(255)          NOT NULL,
    order_date   datetime              NOT NULL,
    total_price  DECIMAL(38, 2)        null DEFAULT 0 NULL,
    created_dt   datetime              NULL,
    updated_dt   datetime              NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE orders_order_line_items
(
    orders_id           BIGINT NOT NULL,
    order_line_items_id BIGINT NOT NULL,
    CONSTRAINT pk_orders_orderlineitems PRIMARY KEY (orders_id, order_line_items_id)
);

