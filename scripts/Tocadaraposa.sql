create database raposa;
use raposa;

create table produtos(
id int auto_increment,
nome varchar(30) not null,
categoria varchar(30) default 'Outros',
preco decimal(5,2) not null,
estoque int default 0,
imagemnome varchar(30),
primary key (id)
);

insert into produtos (nome,preco) values
("Copo Metallica",12.5);

insert into produtos (nome,categoria,preco) values
("Colar de Signo","Joias",15.80),
("Pulseira Caveira","Joias",10);

insert into produtos(nome,preco,estoque)values
("Botas SOAD",59,3);

insert into produtos values
(default,"Escultura de Signo",default,28.9,2,null);

select * from produtos;
