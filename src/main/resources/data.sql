-- =====================
-- CLEAR TABLES
-- =====================
-- Disable FK checks first
-- =====================
-- CLEAR TABLES
-- =====================
-- Use TRUNCATE with RESTART IDENTITY CASCADE to clean data safely.
-- Avoid ALTER TABLE ... DISABLE TRIGGER which may require superuser privileges
-- and can cause the initialization script to fail.
TRUNCATE TABLE wishlist RESTART IDENTITY CASCADE;
TRUNCATE TABLE addresses RESTART IDENTITY CASCADE;
TRUNCATE TABLE profiles RESTART IDENTITY CASCADE;
TRUNCATE TABLE products RESTART IDENTITY CASCADE;
TRUNCATE TABLE categories RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- =====================
-- USERS
-- =====================
INSERT INTO users (name, email, password, role) VALUES
                                                    ('John Doe',      'john.doe@example.com',      'password123', 'USER'),
                                                    ('Jane Smith',    'jane.smith@example.com',     'password456', 'USER'),
                                                    ('Alice Johnson', 'alice.johnson@example.com',  'passAlice789','USER'),
                                                    ('Bob Williams',  'bob.williams@example.com',   'bobPass321',  'USER'),
                                                    ('Charlie Brown', 'charlie.brown@example.com',  'charlie987',  'USER');




-- =====================
-- PROFILES
-- =====================
INSERT INTO profiles (bio, phone_number, date_of_birth, loyalty_points, id)
VALUES
    ('Tech enthusiast', '0712345678', '1998-05-10', 120, 1),
    ('Fashion lover', '0756789123', '2000-08-22', 250, 2),
    ('Gamer and coder', '0789123456', '1997-12-05', 300, 3),
    ('Foodie and traveler', '0765432198', '1999-03-15', 180, 4),
    ('Fitness fanatic', '0745678912', '1995-07-30', 220, 5);

-- Make profiles insert idempotent: if rows with the same id already exist, update them.
INSERT INTO profiles (bio, phone_number, date_of_birth, loyalty_points, id)
VALUES
    ('Tech enthusiast', '0712345678', '1998-05-10', 120, 1),
    ('Fashion lover', '0756789123', '2000-08-22', 250, 2),
    ('Gamer and coder', '0789123456', '1997-12-05', 300, 3),
    ('Foodie and traveler', '0765432198', '1999-03-15', 180, 4),
    ('Fitness fanatic', '0745678912', '1995-07-30', 220, 5)
ON CONFLICT (id) DO UPDATE SET
    bio = EXCLUDED.bio,
    phone_number = EXCLUDED.phone_number,
    date_of_birth = EXCLUDED.date_of_birth,
    loyalty_points = EXCLUDED.loyalty_points;

-- =====================
-- ADDRESSES
-- =====================
INSERT INTO addresses (street, city, state, zip, user_id)
VALUES
    ('123 Main Street', 'Dar es Salaam', 'DSM', '11101', 1),
    ('456 Market Road', 'Arusha', 'AR', '23105', 2),
    ('789 Ocean Drive', 'Mwanza', 'MW', '34512', 3),
    ('321 Hill Street', 'Dodoma', 'DO', '41205', 4),
    ('654 Sunset Blvd', 'Mbeya', 'MB', '56123', 5);

-- =====================
-- CATEGORIES
-- =====================
INSERT INTO categories (name)
VALUES
    ('Electronics'),
    ('Clothing'),
    ('Books'),
    ('Home & Kitchen'),
    ('Sports');

-- =====================
-- PRODUCTS
-- =====================
INSERT INTO products (name, price, description, category_id)
VALUES
    ('Wireless Headphones', 150000.00, 'Bluetooth over-ear headphones', 1),
    ('Denim Jacket', 90000.00, 'Classic blue denim jacket', 2),
    ('Cookbook', 45000.00, 'Healthy recipes cookbook', 3),
    ('Blender', 120000.00, 'High-speed kitchen blender', 4),
    ('Soccer Ball', 35000.00, 'Official size soccer ball', 5);

-- =====================
-- WISHLIST
-- =====================
INSERT INTO wishlist (product_id, user_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5);
