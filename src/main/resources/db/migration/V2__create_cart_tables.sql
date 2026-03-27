/*create table carts
(
    id           uuid default gen_random_uuid()
        not null constraint carts_pk primary key,
    date_created date default current_date
);

CREATE TABLE cart_items (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,  -- auto-increment bigint
                            cart_id UUID NOT NULL,                               -- references carts.id (UUID)
                            product_id BIGINT NOT NULL,                          -- references products.id (bigint)
                            quantity INT DEFAULT 1 NOT NULL,                     -- default quantity

                            CONSTRAINT cart_items_cart_product_unique UNIQUE (cart_id, product_id),

                            CONSTRAINT cart_items_carts_id_fk
                                FOREIGN KEY (cart_id) REFERENCES carts(id)
                                    ON DELETE CASCADE,

                            CONSTRAINT cart_items_products_id_fk
                                FOREIGN KEY (product_id) REFERENCES products(id)
                                    ON DELETE CASCADE
);*/