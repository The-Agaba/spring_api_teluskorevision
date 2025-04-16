-- USERS TABLE
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

-- ADDRESSES TABLE
CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           street VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           zipcode VARCHAR(255) NOT NULL,
                           user_id BIGINT NOT NULL,
                           CONSTRAINT addresses_users_id_fk FOREIGN KEY (user_id) REFERENCES users(id)
);
