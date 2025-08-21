
DROP TABLE IF EXISTS teams;

CREATE TABLE teams (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       team_id VARCHAR(36) NOT NULL UNIQUE,
                       team_name VARCHAR(255) NOT NULL,
                       coach VARCHAR(255) NOT NULL,
                       founding_year INT NOT NULL,
                       budget DECIMAL(15, 2) NOT NULL,
                       team_status VARCHAR(20) DEFAULT 'IS_PLAYING'
);

DROP TABLE IF EXISTS players;


CREATE TABLE players (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         player_id VARCHAR(36) NOT NULL UNIQUE,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         age INT NOT NULL,
                         nationality VARCHAR(100) NOT NULL,
                         jersey_number INT NOT NULL,
                         position VARCHAR(50) NOT NULL,
                         team_id VARCHAR(36) NOT NULL,
                         FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE
);