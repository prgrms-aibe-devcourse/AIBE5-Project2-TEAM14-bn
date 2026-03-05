-- initialization script for Comic Book Rental System
-- sets up application database, copies GCD data and adds rental schema

CREATE DATABASE IF NOT EXISTS comic_rental
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE comic_rental;

-- if the dump created a separate gcd database it will already have run
-- copy the master gcd_series table into this schema so we can add
-- foreign keys and status information without touching the original
DROP TABLE IF EXISTS gcd_series;
CREATE TABLE gcd_series LIKE gcd.gcd_series;
INSERT IGNORE INTO gcd_series SELECT * FROM gcd.gcd_series;

-- track which records are rented in our application
ALTER TABLE gcd_series
    ADD COLUMN IF NOT EXISTS is_rented TINYINT(1) DEFAULT 0;

-- members table (your users)
CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- rentals table (links users to gcd_series)
CREATE TABLE IF NOT EXISTS rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    comic_id INT NOT NULL,
    rented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL 7 DAY),
    returned_at TIMESTAMP NULL,
    status ENUM('RENTED', 'RETURNED', 'OVERDUE') DEFAULT 'RENTED',
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (comic_id) REFERENCES gcd_series(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- a handy view that joins rentals to members and the master series table
CREATE OR REPLACE VIEW rental_overview AS
SELECT r.id           AS rental_id,
       m.name         AS member_name,
       g.name         AS comic_title,
       r.rented_at,
       r.due_date,
       r.returned_at,
       r.status
FROM rentals r
JOIN members m ON r.member_id = m.id
JOIN gcd_series g ON r.comic_id = g.id;

-- legacy schema for small demos is left intact but no longer used by
-- the new rental logic.  You can drop it if you like.
CREATE TABLE IF NOT EXISTS comics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    volume_count INT DEFAULT 0,
    author VARCHAR(255) NOT NULL,
    is_rented BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- sample data for the legacy table, retained for compatibility
INSERT IGNORE INTO comics (title, volume_count, author) VALUES
('Spider-Man', 10, 'Stan Lee'),
('X-Men', 5, 'Chris Claremont'),
('Batman', 8, 'Bob Kane'),
('Superman', 12, 'Jerry Siegel');

