create database raposa;
use raposa;

create table products(
id int auto_increment,
name varchar(30) not null,
category varchar(30) default 'Others',
price decimal(5,2) not null,
inventory int default 0,
imagename varchar(30),
primary key (id)
);

insert into products (name,price) values
("Copo Metallica",12.5);

insert into products (name,category,price) values
("Colar de Signo","Joias",15.80),
("Pulseira Caveira","Joias",10);

insert into products(name,price,inventory)values
("Botas SOAD",59,3);

insert into products values
(default,"Escultura de Signo",default,28.9,2,null);

select * from products;
