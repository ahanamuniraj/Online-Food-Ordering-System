-- ============================================================
-- Team 11 - Online Food Ordering System
-- NOTE: Users are created by DataInitializer.java at startup
--       using live BCrypt encoding — no hardcoded hashes here.
-- ============================================================

-- Restaurants
INSERT INTO restaurant (name, cuisine, rating, open, image_url) VALUES
  ('Spice Garden',  'Indian',    4.5, true,  'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400'),
  ('Pizza Palace',  'Italian',   4.2, true,  'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400'),
  ('Dragon Wok',    'Chinese',   4.0, true,  'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400'),
  ('Burger Barn',   'American',  3.8, false, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400');

-- Menus (one per restaurant)
INSERT INTO menu (restaurant_id) VALUES (1);
INSERT INTO menu (restaurant_id) VALUES (2);
INSERT INTO menu (restaurant_id) VALUES (3);

-- Menu Items — Spice Garden
INSERT INTO menu_item (name, category, price, available, menu_id) VALUES
  ('Butter Chicken',  'Main Course', 220.0, true,  1),
  ('Paneer Tikka',    'Starter',     180.0, true,  1),
  ('Garlic Naan',     'Bread',        50.0, true,  1),
  ('Dal Makhani',     'Main Course', 160.0, true,  1),
  ('Mango Lassi',     'Drinks',       80.0, true,  1);

-- Menu Items — Pizza Palace
INSERT INTO menu_item (name, category, price, available, menu_id) VALUES
  ('Margherita Pizza', 'Pizza',   299.0, true,  2),
  ('Pasta Arrabiata',  'Pasta',   249.0, true,  2),
  ('Garlic Bread',     'Sides',    99.0, true,  2),
  ('Tiramisu',         'Dessert', 199.0, true,  2);

-- Menu Items — Dragon Wok
INSERT INTO menu_item (name, category, price, available, menu_id) VALUES
  ('Kung Pao Chicken', 'Main Course', 260.0, true,  3),
  ('Spring Rolls',     'Starter',     140.0, true,  3),
  ('Fried Rice',       'Rice',        180.0, true,  3),
  ('Wonton Soup',      'Soup',        120.0, true,  3);

-- Coupons
INSERT INTO coupon (code, discount_percent, min_order_amount, expiry_date, active) VALUES
  ('SAVE10',  10.0, 200.0, '2099-12-31', true),
  ('WELCOME', 15.0, 300.0, '2099-12-31', true),
  ('FLAT20',  20.0, 500.0, '2099-12-31', true);
