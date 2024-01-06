--liquibase formatted sql

--changeset sslepnev:1
CREATE UNIQUE INDEX unique_hotel_room_check_in_idx
    ON booking_request (hotel_id, room_id, check_in);