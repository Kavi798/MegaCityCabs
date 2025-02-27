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
    type ENUM('car', 'van', 'bike') NOT NULL,
    status ENUM('available', 'unavailable') NOT NULL DEFAULT 'available'
);

CREATE TABLE Drivers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  -- Store hashed passwords
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE DEFAULT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    vehicle_id INT DEFAULT NULL,
    status ENUM('available', 'busy') NOT NULL DEFAULT 'available',
    FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE SET NULL
);

CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    driver_id INT DEFAULT NULL,  -- Assigned driver (can be NULL initially)
    vehicle_id INT DEFAULT NULL, -- Assigned vehicle (can be NULL initially)
    pickup_location VARCHAR(255) NOT NULL,
    dropoff_location VARCHAR(255) NOT NULL,
    fare DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'accepted', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES Drivers(id) ON DELETE SET NULL,
    FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id) ON DELETE SET NULL
);
