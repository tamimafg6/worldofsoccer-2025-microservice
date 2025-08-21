USE `location-db`;
DROP TABLE IF EXISTS venues;


CREATE TABLE venues (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        venue_id VARCHAR(36) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        capacity INT,
                        city VARCHAR(255),
                        year_built INT,
                        venue_state VARCHAR(255),
                        UNIQUE (venue_id)
);
