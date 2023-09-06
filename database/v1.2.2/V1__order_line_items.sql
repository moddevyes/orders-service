CREATE TABLE order_line_items
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    product_id  BIGINT                NOT NULL,
    quantity    INT                   NOT NULL,
    price       DECIMAL               NOT NULL,
    total_price DECIMAL               NULL,
    created_dt  datetime              NULL,
    updated_dt  datetime              NULL,
    CONSTRAINT pk_order_line_items PRIMARY KEY (id)
);

