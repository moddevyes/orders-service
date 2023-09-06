USE ecommerce_orders_db;

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

CREATE TABLE orders
(
    id                         BIGINT AUTO_INCREMENT NOT NULL,
    orders_account_id          BIGINT                NOT NULL,
    order_number               VARCHAR(255)          NOT NULL,
    order_date                 datetime              NOT NULL,
    orders_shipping_address_id BIGINT                NULL,
    total_price                DECIMAL(38, 2)        null DEFAULT 0 NULL,
    created_dt                 datetime              NULL,
    updated_dt                 datetime              NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE orders_order_line_items
(
    orders_id           BIGINT NOT NULL,
    order_line_items_id BIGINT NOT NULL,
    CONSTRAINT pk_orders_orderlineitems PRIMARY KEY (orders_id, order_line_items_id)
);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT uc_orders_order_line_items_orderlineitems UNIQUE (order_line_items_id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_ORDERSACCOUNT FOREIGN KEY (orders_account_id) REFERENCES orders_account (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_ORDERSSHIPPINGADDRESS FOREIGN KEY (orders_shipping_address_id) REFERENCES orders_address (id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_order_line_items FOREIGN KEY (order_line_items_id) REFERENCES order_line_items (id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_orders FOREIGN KEY (orders_id) REFERENCES orders (id);