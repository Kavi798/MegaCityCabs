CREATE SCHEMA `mega_city_cabs` ;
-- Use the correct database
USE mega_city_cabs;
DROP TABLE Users;
-- Create Users Table (Merged with Customer Information)
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- Store hashed passwords
    role ENUM('adm', 'cus') NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT DEFAULT NULL,
    phone VARCHAR(15) UNIQUE DEFAULT NULL,
    nic VARCHAR(20) UNIQUE DEFAULT NULL
);
INSERT INTO Users (email, username, password, role, name, address, phone, nic) 
VALUES ('kavi@gamail.com', 'admin', '$2a$12$9GjRRzyu185OfH0uuvz.1uAei8QKN2aQowrNeOjMnSXIR7V2YrO9e', 'adm', 'Kavindu', 'Colombo', '0719090890', '2000789007');

CREATE TABLE Vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    driver_id INT DEFAULT NULL,
    type ENUM('Sedan', 'SUV', 'Van', 'Luxury') NOT NULL,
    status ENUM('available', 'unavailable') NOT NULL DEFAULT 'available'
);
INSERT INTO Vehicles (model, plate_number, capacity, type, status) 
VALUES 
    ('Toyota Corolla', 'ABC-1234', 4, 'Sedan', 'available'),
    ('Honda CR-V', 'XYZ-5678', 5, 'SUV', 'available'),
	('BMW X5', 'GHI-7890', 5, 'SUV', 'unavailable'),
    ('Audi A8', 'JKL-1122', 4, 'Luxury', 'available'),
    ('Mercedes-Benz S-Class', 'LMN-9012', 4, 'Luxury', 'unavailable');

CREATE TABLE Drivers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dName VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(15) UNIQUE DEFAULT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
	nic VARCHAR(100) NOT NULL,
    dstatus ENUM('available', 'busy') NOT NULL DEFAULT 'available'
);
INSERT INTO Drivers (dName, phone, license_number, nic, vehicle_id, dstatus) 
VALUES 
('John Doe', '555-1234', 'LN-987654', '123456789V', 1, 'available'),
('Alice Smith', '555-5678', 'LN-456789', '987654321V', 2, 'available'),
('Michael Johnson', '555-9876', 'LN-123456', '654321987V', 3, 'busy'),
('Emily Brown', '555-4321', 'LN-654321', '789456123V', NULL, 'available'),
('David Williams', '555-8765', 'LN-789123', '159753852V', 4, 'available');



CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    vehicle_id INT DEFAULT NULL, -- Assigned vehicle (can be NULL initially)
    pickup_date DATE NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    dropoff_location VARCHAR(255) NOT NULL,
    fare DECIMAL(10,2) NOT NULL,
    bstatus ENUM('pending', 'accepted', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES Drivers(id) ON DELETE SET NULL,
    FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE SET NULL
);
