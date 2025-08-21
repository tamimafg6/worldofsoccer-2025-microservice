
INSERT INTO leagues
(league_id, name, country, format, number_of_teams, league_difficulty,
 season_year, season_start_date, season_end_date,
 competition_format_type, competition_format_group_stage, competition_format_knockout)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Premier League', 'England', 'LEAGUE', 20, 'High',
     2024, '2024-08-01', '2025-05-15', 'LEAGUE', false, false),
    ('22222222-2222-2222-2222-222222222222', 'La Liga', 'Spain', 'LEAGUE', 20, 'High',
     2024, '2024-08-10', '2025-05-20', 'LEAGUE', false, false),
    ('33333333-3333-3333-3333-333333333333', 'Bundesliga', 'Germany', 'LEAGUE', 18, 'Medium',
     2024, '2024-08-15', '2025-05-25', 'LEAGUE', false, false),
    ('44444444-4444-4444-4444-444444444444', 'Serie A', 'Italy', 'LEAGUE', 20, 'High',
     2024, '2024-08-20', '2025-05-30', 'LEAGUE', false, false),
    ('55555555-5555-5555-5555-555555555555', 'Ligue 1', 'France', 'LEAGUE', 20, 'Medium',
     2024, '2024-08-05', '2025-05-10', 'LEAGUE', false, false),
    ('66666666-6666-6666-6666-666666666666', 'Champions League', 'Europe', 'CUP', 32, 'Very High',
     2024, '2024-09-01', '2025-06-01', 'CUP', true, true),
    ('77777777-7777-7777-7777-777777777777', 'Copa Libertadores', 'South America', 'CUP', 32, 'High',
     2024, '2024-09-05', '2025-06-05', 'CUP', true, true),
    ('88888888-8888-8888-8888-888888888888', 'MLS', 'USA', 'LEAGUE', 27, 'Medium',
     2024, '2024-08-25', '2025-05-18', 'LEAGUE', false, false),
    ('99999999-9999-9999-9999-999999999999', 'Eredivisie', 'Netherlands', 'LEAGUE', 18, 'Medium',
     2024, '2024-08-30', '2025-05-22', 'LEAGUE', false, false),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Primeira Liga', 'Portugal', 'LEAGUE', 18, 'Medium',
     2024, '2024-09-10', '2025-06-10', 'LEAGUE', false, false);
