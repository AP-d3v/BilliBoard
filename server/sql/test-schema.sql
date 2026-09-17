

DROP DATABASE IF EXISTS billiboard_test;
CREATE DATABASE billiboard_test;
USE billiboard_test;


CREATE TABLE bar_owner (
    bar_owner_id INT AUTO_INCREMENT PRIMARY KEY,
    email        VARCHAR(250) UNIQUE NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    password     VARCHAR(255) NOT NULL
);

CREATE TABLE bar (
    bar_id       INT AUTO_INCREMENT PRIMARY KEY,
    bar_name     VARCHAR(250) NOT NULL,
    address      VARCHAR(250) NOT NULL,
    bar_owner_id INT NOT NULL,
    CONSTRAINT fk_bar_bar_owner
        FOREIGN KEY (bar_owner_id) REFERENCES bar_owner (bar_owner_id)
);

CREATE TABLE billiard_table (
    table_id     INT AUTO_INCREMENT PRIMARY KEY,
    max_players  INT NOT NULL,
    closing_time TIME NOT NULL,
    bar_id       INT NOT NULL,
    CONSTRAINT fk_billiard_table_bar
        FOREIGN KEY (bar_id) REFERENCES bar (bar_id)
);

CREATE TABLE reservation (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    player_name    VARCHAR(250) NOT NULL,
    patron_email   VARCHAR(250) NOT NULL,
    session_id     VARCHAR(50) NOT NULL,
    table_id       INT NOT NULL,
    status               VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    confirm_requested_at DATETIME NULL,
    nudged_by_session    VARCHAR(50) NULL,
    onesignal_subscription_id VARCHAR(250) NULL,
    CONSTRAINT fk_reservation_billiard_table
        FOREIGN KEY (table_id) REFERENCES billiard_table (table_id)
);


DELIMITER //
CREATE PROCEDURE set_known_good_state()
BEGIN
    
    DELETE FROM reservation;
    DELETE FROM billiard_table;
    DELETE FROM bar;
    DELETE FROM bar_owner;
    ALTER TABLE reservation     AUTO_INCREMENT = 1;
    ALTER TABLE billiard_table  AUTO_INCREMENT = 1;
    ALTER TABLE bar             AUTO_INCREMENT = 1;
    ALTER TABLE bar_owner       AUTO_INCREMENT = 1;

    INSERT INTO bar_owner (email, first_name, last_name, password)
    VALUES ('aprescott@dev10.com', 'Alasco', 'Prescott', '-1424436561');


    INSERT INTO bar (bar_name, address, bar_owner_id) VALUES
        ('Carmelos', '1234 Main St', 1),
        ('Paddy''s Pub', '123 5th Ave', 1);


    INSERT INTO billiard_table (max_players, closing_time, bar_id) VALUES
        (4, '23:00:00', 1),
        (2, '01:00:00', 1),
        (4, '00:00:00', 2);

    INSERT INTO reservation (player_name, patron_email, session_id, table_id, status) VALUES
        ('Ada', 'ada@example.com', 'seed-session-ada', 1, 'PLAYING'),
        ('Grace', 'grace@example.com', 'seed-session-grace', 1, 'WAITING');
END//
DELIMITER ;
