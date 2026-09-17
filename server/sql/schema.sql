

DROP DATABASE IF EXISTS billiboard;
CREATE DATABASE billiboard;
USE billiboard;


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
