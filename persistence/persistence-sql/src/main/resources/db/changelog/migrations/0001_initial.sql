--liquibase formatted sql

--changeset system:1 dbms:postgresql
CREATE TABLE chess_games
(
	id   TEXT PRIMARY KEY,
	game JSONB NOT NULL
);
