-- initialization script for Comic Book Rental System
-- creates schema, tables, indexes, foreign keys and seeds some sample data

CREATE DATABASE IF NOT EXISTS comic_rental
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE comic_rental;

-- comics table
CREATE TABLE IF NOT EXISTS comics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    volume_count INT DEFAULT 0,
    author VARCHAR(255) NOT NULL,
    is_rented BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX(idx_title) (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- members table
CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    join_date DATE DEFAULT CURRENT_DATE,
    INDEX(idx_name) (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- rentals table
CREATE TABLE IF NOT EXISTS rentals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comic_id INT NOT NULL,
    member_id INT NOT NULL,
    rented_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    returned_at DATETIME NULL,
    status ENUM('RENTED','RETURNED') NOT NULL DEFAULT 'RENTED',
    INDEX(idx_status) (status),
    FOREIGN KEY (comic_id) REFERENCES comics(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- seed data: a few Marvel and DC comics, members and rentals
INSERT INTO comics (title, volume_count, author) VALUES
('Spider-Man', 10, 'Stan Lee'),
('X-Men', 5, 'Chris Claremont'),
('Batman', 8, 'Bob Kane'),
('Superman', 12, 'Jerry Siegel');

INSERT INTO members (name, phone_number) VALUES
('Peter Parker', '555-0101'),
('Bruce Wayne', '555-0202');

-- example rental: Peter rents Spider-Man
INSERT INTO rentals (comic_id, member_id) VALUES
((SELECT id FROM comics WHERE title='Spider-Man'),
 (SELECT id FROM members WHERE name='Peter Parker'));

-- another rental already returned
INSERT INTO rentals (comic_id, member_id, returned_at, status) VALUES
((SELECT id FROM comics WHERE title='Batman'),
 (SELECT id FROM members WHERE name='Bruce Wayne'),
 NOW(), 'RETURNED');
