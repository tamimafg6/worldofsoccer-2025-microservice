USE `league-db`;
DROP TABLE IF EXISTS leagues;

CREATE TABLE leagues (
                         league_id VARCHAR(36) NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         country VARCHAR(255) NOT NULL,
                         format VARCHAR(20) NOT NULL,
                         number_of_teams INT,
                         league_difficulty VARCHAR(255),
                         season_year INT,
                         season_start_date DATE,
                         season_end_date DATE,
                         competition_format_type VARCHAR(20),
                         competition_format_group_stage BOOLEAN,
                         competition_format_knockout BOOLEAN,
                         PRIMARY KEY (league_id)
);
