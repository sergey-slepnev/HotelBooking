--liquibase formatted sql

--changeset sslepnev:1
ALTER TABLE booking_request
    DROP CONSTRAINT booking_request_room_id_fkey;

--changeset sslepnev:2
ALTER TABLE booking_request
    ADD CONSTRAINT booking_request_room_id_fkey
        FOREIGN KEY (room_id)
            REFERENCES room (id)
            ON DELETE CASCADE