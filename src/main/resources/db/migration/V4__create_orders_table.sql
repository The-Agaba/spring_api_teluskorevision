CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,

                        customer_id BIGINT NOT NULL,
                        status VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        total_price NUMERIC(10,2) NOT NULL,

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id)
                                REFERENCES users(id)
);
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,

                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,

                             unit_price NUMERIC(10,2) NOT NULL,
                             quantity INTEGER NOT NULL,
                             total_price NUMERIC(10,2) NOT NULL,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id)
                                     REFERENCES orders(id),

                             CONSTRAINT fk_order_items_product
                                 FOREIGN KEY (product_id)
                                     REFERENCES products(id)
);