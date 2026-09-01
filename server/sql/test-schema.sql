DROP DATABASE IF EXISTS billiboard_test;
CREATE DATABASE billiboard_test;
use billiboard_test;


create table bar_owner(
bar_owner_id int auto_increment primary key,
email varchar(250) UNIQUE not null,
first_name varchar(100) not null,
last_name varchar(100) not null,
password int Not Null
);

create table bar(
bar_id int auto_increment primary key,
bar_name varchar(250) not null ,
address varchar(250) not null,
bar_owner_id int,
foreign key(bar_owner_id)
	references bar_owner(bar_owner_id)
)

create table billiard_table(
table_id int auto_increment primary key,
max_players int not null,
closing_time Time not null,
bar_id int not null,
foreign key(bar_id)
	references bar(bar_id)
)

create table reservation(
reservation_id int auto_increment primary key,
player_name varchar(250) not null,
patron_email varchar(250) not null,
session_id int not null,
table_id int not null,
foreign key(table_id)
	references billiard_table(table_id)
)

create table notification(
notification_id int auto_increment primary key,
reservation_id int not null,
oneSignal_subscription_id  int not null,
foreign key(reservation_id)
	references reservation(reservation_id)
)


delimiter //
create procedure set_known_good_state()
BEGIN
	DELETE FROM bar_owner;
	DELETE FROM bar;
	DELETE FROM billiard_table;
	DELETE FROM reservation;
	DELETE FROM notification;
	alter table bar_owner auto_increment=1;
	alter table bar auto_increment=1;
	alter table billiard_table auto_increment=1;
	alter table reservation auto_increment=1;
	alter table notification auto_increment=1;

INSERT INTO bar_owner (email,first_name,last_name,password)
VALUES
('aprescott@dev10.com','Alasco','Prescott','abc123');


END//
delimiter ;
