DROP IF EXISTS billiboard;
CREATE DATABASE billiboard;
use billiboard;


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
foreign key(bar_owner)
	references bar_owner(bar_owner_id)
)

create table billiard_table(
table_id int auto_increment primary key,
max_players int not null,
closing_time Time not null,
bar_id int not null,
foreign key(bar)
	references bar(bar_id)
)

create table reservation(
reservation_id int auto_increment primary key,
player_name varchar(250) not null,
patron_email varchar(250) not null,
session_id int not null,
table_id int not null,
foreign key(billard_table)
	references billard_table(table_id)
)

create table notification(
notification_id int auto_increment primary key,
reservation_id int not null,
oneSignal_subscription_id  int not null,
foreign key(reservation)
	references reservation(reservation_id)
)





