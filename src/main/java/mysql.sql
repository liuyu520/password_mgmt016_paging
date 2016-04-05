create database passwd3;
use passwd3
create table pass(id int primary key auto_increment,
	title varchar(50) unique,
	username varchar(50),
	pwd varchar(100),
	description longtext,
	expiration_time char(12) default NULL,
	createTime datetime, 
	status  tinyint(4)
);


create table t_user(id int primary key auto_increment,username char(20) unique,password char(50));
insert into t_user(username,password) values('fM1ka3aJMFE=','fM1ka3aJMFE=');--root

--create table t_ciphertext(id int primary key auto_increment,ciphertext2 TEXT);