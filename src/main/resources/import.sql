INSERT INTO car_color (color) VALUE ('BLACK');
INSERT INTO car_color (color) VALUE ('WHITE');
INSERT INTO car_color (color) VALUE ('YELLOW');

INSERT INTO car_brands (brand) VALUE ('BMW');
INSERT INTO car_brands (brand) VALUE ('Audi');

INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('AA 1111 AA', 1, 'A6', 1, 120, 'A', 'NOT_RENTED');
INSERT INTO cars (number, car_brands_id, name, car_color_id, price_per_day, segment, status) VALUES ('BB 1111 AC', 2, 'Q7', 2, 100, 'A', 'RENTED');


