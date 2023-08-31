create schema ecommerce_orders_db;
use ecommerce_orders_db;

CREATE TABLE order_line_items
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    order_id    BIGINT                NOT NULL,
    product_id  BIGINT                NOT NULL,
    shipment_id BIGINT                NULL,
    quantity    INT                   NOT NULL,
    price       DECIMAL(38, 2)        NOT NULL,
    total_price DECIMAL(38, 2)        null DEFAULT 0 NULL,
    created_dt  datetime              NULL,
    updated_dt  datetime              NULL,
    CONSTRAINT pk_order_line_items PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    account_id          BIGINT                NOT NULL,
    order_number        VARCHAR(255)          NOT NULL,
    order_date          datetime              NOT NULL,
    shipping_address_id BIGINT                NULL,
    total_price         DECIMAL(38, 2)        null DEFAULT 0 NULL,
    created_dt          datetime              NULL,
    updated_dt          datetime              NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE orders_order_line_items
(
    orders_id           BIGINT NOT NULL,
    order_line_items_id BIGINT NOT NULL,
    CONSTRAINT pk_orders_orderlineitems PRIMARY KEY (orders_id, order_line_items_id)
);

ALTER TABLE orders
    ADD CONSTRAINT uc_order_num UNIQUE (order_number);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT uc_orders_order_line_items_orderlineitems UNIQUE (order_line_items_id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_order_line_items
        FOREIGN KEY (order_line_items_id)
            REFERENCES order_line_items (id)
            ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_orders
        FOREIGN KEY (orders_id)
            REFERENCES orders (id)
            ON UPDATE CASCADE ON DELETE CASCADE;