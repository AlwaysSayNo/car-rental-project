# COLORS
INSERT INTO car_color (color) VALUE ('BLACK');
INSERT INTO car_color (color) VALUE ('WHITE');
INSERT INTO car_color (color) VALUE ('YELLOW');
INSERT INTO car_color (color) VALUE ('RED');

# BRANDS
INSERT INTO car_brands (brand) VALUE ('BMW');
INSERT INTO car_brands (brand) VALUE ('Audi');
INSERT INTO car_brands (brand) VALUE ('Ford');
INSERT INTO car_brands (brand) VALUE ('Hyundai');

# BRANDS
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('AA 1111 AA', 1, 'X6', 1, 600, 'S', 'NOT_RENTED');
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('BB 1111 BB', 2, 'A6', 2, 450, 'E', 'ON_HOLD');
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('EE 1111 EE', 3, 'Taurus', 2, 400, 'E', 'NOT_RENTED');
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('CC 1111 CC', 4, 'Aura', 3, 200, 'B', 'NOT_RENTED');
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('DD 1111 DD', 4, 'i30', 4, 300, 'C', 'NOT_RENTED');

# USERS (password=user)
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('user1@gmail.com', 'User', 'One', '$2a$12$yvdjbqbVG7XU3zMYR5jGvetqy6dn8IfAEYs7m6UKYoZGG/HRzFYMa', '+380 50 111 11 11', 'ROLE_USER', 'ON_HOLD');
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('user2@gmail.com', 'User', 'Two', '$2a$12$kViX0LO6nQgmOSeWFOtjE.EUGXs3MNaaxCXM3nxhBcUHXssmCi1ey', '+380 50 222 22 22', 'ROLE_USER', 'ACTIVE');

# MANAGER (password=manager)
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('manager1@gmail.com', 'Manager', 'One', '$2a$12$Jutrx0iYEyOUtFoWiq/c3OnW39COjtxCpzN3hdgpg3gqPf1EGjaHi', '+380 95 111 11 11', 'ROLE_MANAGER', 'ACTIVE');
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('manager2@gmail.com', 'Manager', 'Two', '$2a$12$KpxKSwlHyF5ISkf8Eu.ANuGPkGAjTWldl5plL5oLRCaXEcKLQAt/W', '+380 95 222 22 22', 'ROLE_MANAGER', 'ACTIVE');

# ADMIN (password=admin)
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('admin1@gmail.com', 'Admin', 'One', '$2a$12$5neMW8gaSLKtF0ywADDFP.G/fIhVBP98SGb2H97yLnEMWC9LM48mO', '+380 66 111 11 11', 'ROLE_ADMIN', 'ACTIVE');

# ORDER
INSERT INTO orders (user_id, car_id, status) VALUE (1, 2, 'REPAIR_PAYMENT');

# PAYMENT DETAILS
INSERT INTO payment_details(passport_number, first_name, last_name, date_of_birth) VALUE ('000000000', 'User', 'One', '2000-01-01');

# BILL
INSERT INTO bills (order_id, payment_details_id, start_date, expiration_date, car_price, with_driver, driver_price, total_price, status) VALUE (1, 1, '2022-02-11', '2022-02-12', 100, false, 0, 100, 'PAID');

# BREAKDOWN
INSERT INTO breakdowns (message, price, status, order_id, payment_details_id) VALUE ('', 100, 'NOT_PAID', 1, null);