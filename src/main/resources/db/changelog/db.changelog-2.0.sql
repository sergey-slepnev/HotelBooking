--liquibase formatted sql

--changeset sslepnev:1
ALTER TABLE hotel_details
    DROP CONSTRAINT hotel_details_hotel_id_key;