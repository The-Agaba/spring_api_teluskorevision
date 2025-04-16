-- USERS
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

-- PROFILES
CREATE TABLE profiles (
                          id BIGINT PRIMARY KEY,
                          bio TEXT,
                          phone_number VARCHAR(15),
                          date_of_birth DATE,
                          loyalty_points INT DEFAULT 0,
                          CONSTRAINT profiles_user_fk FOREIGN KEY (id) REFERENCES users(id)
);

-- ADDRESSES
CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           street VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           state VARCHAR(255) NOT NULL,
                           zip VARCHAR(255) NOT NULL,
                           user_id BIGINT NOT NULL,
                           CONSTRAINT addresses_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);

-- CATEGORIES
CREATE TABLE categories (
                            id SMALLSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL
);

-- PRODUCTS
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          price NUMERIC(10,2) NOT NULL,
                          description TEXT NOT NULL,
                          category_id SMALLINT,
                          CONSTRAINT products_category_fk FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_products_category_id ON products(category_id);

-- WISHLIST
CREATE TABLE wishlist (
                          product_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          PRIMARY KEY (product_id, user_id),
                          CONSTRAINT wishlist_product_fk FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                          CONSTRAINT wishlist_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_wishlist_user_id ON wishlist(user_id);


