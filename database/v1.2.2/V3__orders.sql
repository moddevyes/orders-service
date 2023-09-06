CREATE TABLE orders
(
    orders_id                  BIGINT AUTO_INCREMENT NOT NULL,
    orders_account_id          BIGINT                NULL,
    order_number               VARCHAR(255)          NOT NULL,
    order_date                 datetime              NOT NULL,
    orders_shipping_address_id BIGINT                NULL,
    total_price                DECIMAL(38, 2)        null DEFAULT 0 NULL,
    created_dt                 datetime              NULL,
    updated_dt                 datetime              NULL,
    CONSTRAINT pk_orders PRIMARY KEY (orders_id)
);

CREATE TABLE orders_order_line_items
(
    orders_orders_id    BIGINT NOT NULL,
    order_line_items_id BIGINT NOT NULL,
    CONSTRAINT pk_orders_orderlineitems PRIMARY KEY (orders_orders_id, order_line_items_id)
);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT uc_orders_order_line_items_ororidorid UNIQUE (orders_orders_id, order_line_items_id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_ORDERSACCOUNT FOREIGN KEY (orders_account_id) REFERENCES orders_account (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_ORDERSSHIPPINGADDRESS FOREIGN KEY (orders_shipping_address_id) REFERENCES orders_address (id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_order_line_items FOREIGN KEY (order_line_items_id) REFERENCES order_line_items (id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_orders FOREIGN KEY (orders_orders_id) REFERENCES orders (orders_id);