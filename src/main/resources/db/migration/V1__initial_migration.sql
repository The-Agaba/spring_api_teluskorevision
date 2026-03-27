
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL,
                                     password VARCHAR(255) NOT NULL
);

-- PROFILES
CREATE TABLE IF NOT EXISTS profiles (
                                        id BIGINT PRIMARY KEY,
                                        bio TEXT,
                                        phone_number VARCHAR(15),
                                        date_of_birth DATE,
                                        loyalty_points INT DEFAULT 0,
                                        CONSTRAINT profiles_user_fk FOREIGN KEY (id) REFERENCES users(id)
);

-- ADDRESSES
CREATE TABLE IF NOT EXISTS addresses (
                                         id BIGSERIAL PRIMARY KEY,
                                         street VARCHAR(255) NOT NULL,
                                         city VARCHAR(255) NOT NULL,
                                         state VARCHAR(255) NOT NULL,
                                         zip VARCHAR(255) NOT NULL,
                                         user_id BIGINT NOT NULL,
                                         CONSTRAINT addresses_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);

-- CATEGORIES
CREATE TABLE IF NOT EXISTS categories (
                                          id SMALLSERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL
);

-- PRODUCTS
CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        price NUMERIC(10,2) NOT NULL,
                                        description TEXT NOT NULL,
                                        category_id SMALLINT,
                                        CONSTRAINT products_category_fk FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);

-- WISHLIST
CREATE TABLE IF NOT EXISTS wishlist (
                                        product_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        PRIMARY KEY (product_id, user_id),
                                        CONSTRAINT wishlist_product_fk FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                        CONSTRAINT wishlist_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_wishlist_user_id ON wishlist(user_id);

-- CARTS
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS carts (
                                     id UUID DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
                                     date_created DATE DEFAULT current_date
);

-- CART ITEMS
CREATE TABLE IF NOT EXISTS cart_items (
                                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                          cart_id UUID NOT NULL,
                                          product_id BIGINT NOT NULL,
                                          quantity INT DEFAULT 1 NOT NULL,
                                          CONSTRAINT cart_items_cart_product_unique UNIQUE (cart_id, product_id),
                                          CONSTRAINT cart_items_carts_id_fk FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
                                          CONSTRAINT cart_items_products_id_fk FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
