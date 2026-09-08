
USE billiboard;

INSERT INTO bar_owner (email, first_name, last_name, password)
VALUES ('aprescott@dev10.com', 'Alasco', 'Prescott', '-1424436561');


INSERT INTO bar (bar_name, address, bar_owner_id) VALUES
    ('Carmelos', '1234 Main St', 1),
    ('Paddy''s Pub', '123 5th Ave', 1);

INSERT INTO billiard_table (max_players, closing_time, bar_id) VALUES
    (4, '23:00:00', 1),
    (2, '01:00:00', 1),
    (4, '00:00:00', 2);

INSERT INTO reservation (player_name, patron_email, session_id, table_id) VALUES
    ('Ada', 'ada@gmail.com', 'seed-session-ada', 1),
    ('Grace', 'grace@example.com', 'seed-session-grace', 1);


