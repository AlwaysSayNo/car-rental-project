# CARS
INSERT INTO car_color (color) VALUE ('BLACK');
INSERT INTO car_color (color) VALUE ('WHITE');
INSERT INTO car_color (color) VALUE ('YELLOW');

INSERT INTO car_brands (brand) VALUE ('BMW');
INSERT INTO car_brands (brand) VALUE ('Audi');

INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('AA 1111 AA', 1, 'A6', 1, 120, 'A', 'NOT_RENTED');
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('BB 1111 AC', 2, 'Q7', 2, 100, 'A', 'RENTED');

# USERS (password=user)
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('user1@gmail.com', 'User', 'One', '$2a$12$yvdjbqbVG7XU3zMYR5jGvetqy6dn8IfAEYs7m6UKYoZGG/HRzFYMa', '+380 50 111 11 11', 'ROLE_USER', 'ACTIVE');
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('user2@gmail.com', 'User', 'Two', '$2a$12$kViX0LO6nQgmOSeWFOtjE.EUGXs3MNaaxCXM3nxhBcUHXssmCi1ey', '+380 50 222 22 22', 'ROLE_USER', 'ACTIVE');

# MANAGER (password=manager)
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('manager1@gmail.com', 'Manager', 'One', '$2a$12$Jutrx0iYEyOUtFoWiq/c3OnW39COjtxCpzN3hdgpg3gqPf1EGjaHi', '+380 95 111 11 11', 'ROLE_MANAGER', 'ACTIVE');
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('manager2@gmail.com', 'Manager', 'Two', '$2a$12$KpxKSwlHyF5ISkf8Eu.ANuGPkGAjTWldl5plL5oLRCaXEcKLQAt/W', '+380 95 222 22 22', 'ROLE_MANAGER', 'ACTIVE');

# ADMIN (password=admin)
INSERT INTO users (email, first_name, last_name, password, phone_number, role, status) VALUE ('admin1@gmail.com', 'Admin', 'One', '$2a$12$5neMW8gaSLKtF0ywADDFP.G/fIhVBP98SGb2H97yLnEMWC9LM48mO', '+380 66 111 11 11', 'ROLE_ADMIN', 'ACTIVE');