-- ALTERS
USE ecommerce_orders_db;

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT uc_orders_account_addresses_addresses UNIQUE (addresses_id);

ALTER TABLE orders_account
    ADD CONSTRAINT uc_orders_account_emailaddress UNIQUE (email_address);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT fk_ordaccadd_on_orders_account FOREIGN KEY (orders_account_id) REFERENCES orders_account (id);

ALTER TABLE orders_account_addresses
    ADD CONSTRAINT fk_ordaccadd_on_orders_address FOREIGN KEY (addresses_id) REFERENCES orders_address (id);

ALTER TABLE orders_account_orders
    ADD CONSTRAINT fk_ordaccord_on_orders FOREIGN KEY (orders_id) REFERENCES orders (id);

ALTER TABLE orders_account_orders
    ADD CONSTRAINT fk_ordaccord_on_orders_account FOREIGN KEY (orders_account_id) REFERENCES orders_account (id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT uc_orders_order_line_items_oridorid UNIQUE (orders_id, order_line_items_id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_order_line_items FOREIGN KEY (order_line_items_id) REFERENCES order_line_items (id);

ALTER TABLE orders_order_line_items
    ADD CONSTRAINT fk_ordordlinite_on_orders FOREIGN KEY (orders_id) REFERENCES orders (id);

ALTER TABLE orders_address_orders
    ADD CONSTRAINT fk_ordaddord_on_orders FOREIGN KEY (orders_id) REFERENCES orders (id);

ALTER TABLE orders_address_orders
    ADD CONSTRAINT fk_ordaddord_on_orders_address FOREIGN KEY (orders_address_id) REFERENCES orders_address (id);

ALTER TABLE orders_address
    ADD COLUMN     created_dt   datetime              NULL;

ALTER TABLE orders_address
    ADD COLUMN     updated_dt   datetime              NULL;
