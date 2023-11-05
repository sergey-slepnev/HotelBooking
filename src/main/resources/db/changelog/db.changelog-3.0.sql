--liquibase formatted sql

--changeset sslepnev:1
ALTER TABLE room
    ALTER COLUMN description DROP NOT NULL;