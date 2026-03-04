-- Schema for Comic Book Rental System

CREATE DATABASE IF NOT EXISTS comic_rental;
USE comic_rental;

-- Comics table
CREATE TABLE IF NOT EXISTS comics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    volume_count INT DEFAULT 0,
    author VARCHAR(255) NOT NULL,
    is_rented BOOLEAN DEFAULT FALSE
);

-- Members table
CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    join_date DATE DEFAULT CURRENT_DATE
);

-- Rentals table
CREATE TABLE IF NOT EXISTS rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comic_id INT NOT NULL,
    member_id INT NOT NULL,
    rented_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    returned_at DATETIME NULL,
    status ENUM('RENTED','RETURNED') NOT NULL DEFAULT 'RENTED',
    FOREIGN KEY (comic_id) REFERENCES comics(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);
