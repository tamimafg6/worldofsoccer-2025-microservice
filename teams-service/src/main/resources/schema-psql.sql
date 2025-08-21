DROP TABLE IF EXISTS teams CASCADE;

DROP TABLE IF EXISTS players;

CREATE TABLE teams (
                       id SERIAL ,
                       team_id VARCHAR(36) NOT NULL UNIQUE,
                       team_name VARCHAR(255) NOT NULL,
                       coach VARCHAR(255) NOT NULL,
                       founding_year INT NOT NULL,
                       budget DECIMAL(15, 2) NOT NULL,
                       team_status VARCHAR(20) DEFAULT 'IS_PLAYING',
                       PRIMARY KEY (id)

);

CREATE TABLE players (
                         id SERIAL ,
                         player_id VARCHAR(36) NOT NULL UNIQUE,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         age INT NOT NULL,
                         nationality VARCHAR(100) NOT NULL,
                         jersey_number INT NOT NULL,
                         position VARCHAR(50) NOT NULL,
                         team_id VARCHAR(36) NOT NULL,
                         FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE,
                         PRIMARY KEY (id)
);
