create table pessoa(
	id integer not null primary key,
	nome varchar(60) not null,
	cpf varchar(11) not null,
	endereco varchar(120),
	sexo char(1) not null
);

insert into pessoa(id, nome, cpf, sexo) values (1, 'Wandergleidison da Silva', '11111111111', 'M');

create table usuario (
	id integer not null primary key,
	login varchar (20) not null,
	senha varchar (20) not null,
	idpessoa integer not null
);

alter table usuario add CONSTRAINT usuario_pessoa FOREIGN KEY (idpessoa) REFERENCES PESSOA(ID);