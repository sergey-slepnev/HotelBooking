--liquibase formatted sql

--changeset sslepnev:1
CREATE TABLE IF NOT EXISTS users
(
    id            SERIAL PRIMARY KEY,
    role          VARCHAR(64)  NOT NULL,
    username      VARCHAR(128) NOT NULL UNIQUE,
    password      VARCHAR(128) NOT NULL,
    firstname     VARCHAR(128) NOT NULL,
    lastname      VARCHAR(128) NOT NULL,
    birth_date    DATE,
    phone         VARCHAR(32)  NOT NULL UNIQUE,
    image         VARCHAR(1024),
    status        VARCHAR(32)  NOT NULL,
    registered_at TIMESTAMP    NOT NULL
);
--rollback DROP TABLE users;

--changeset sslepnev:2
CREATE TABLE IF NOT EXISTS hotel
(
    id        SERIAL PRIMARY KEY,
    owner_id  INT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    name      VARCHAR(128)                                NOT NULL,
    available BOOLEAN                                     NOT NULL,
    status    VARCHAR(64)                                 NOT NULL
);
--rollback DROP TABLE hotel;

--changeset sslepnev:3
CREATE TABLE IF NOT EXISTS hotel_details
(
    id           SERIAL PRIMARY KEY,
    hotel_id     INT          NOT NULL UNIQUE REFERENCES hotel (id),
    phone_number VARCHAR(32)  NOT NULL UNIQUE,
    country      VARCHAR(128) NOT NULL,
    locality     VARCHAR(128) NOT NULL,
    area         VARCHAR(128) NOT NULL,
    street       VARCHAR(128) NOT NULL,
    floor_count  INT          NOT NULL,
    star         VARCHAR(16)  NOT NULL,
    description  TEXT         NOT NULL
);
--rollback DROP TABLE hotel_details;

--changeset sslepnev:4
CREATE TABLE IF NOT EXISTS room
(
    id                 SERIAL PRIMARY KEY,
    hotel_id           INT REFERENCES hotel (id) ON DELETE CASCADE NOT NULL,
    room_no            INT                                         NOT NULL,
    type               VARCHAR(64)                                 NOT NULL,
    square             FLOAT                                       NOT NULL,
    adult_bed_count    INT                                         NOT NULL,
    children_bed_count INT                                         NOT NULL,
    cost               NUMERIC(10, 2)                              NOT NULL,
    available          BOOLEAN                                     NOT NULL,
    floor              INT                                         NOT NULL,
    description        TEXT                                        NOT NULL
);
--rollback DROP TABLE room;

--changeset sslepnev:5
CREATE TABLE IF NOT EXISTS booking_request
(
    id         BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP                 NOT NULL,
    hotel_id   INT REFERENCES hotel (id) NOT NULL,
    room_id    INT REFERENCES room (id)  NOT NULL,
    user_id    INT REFERENCES users (id) NOT NULL,
    check_in   DATE                      NOT NULL,
    check_out  DATE                      NOT NULL,
    status     VARCHAR(32)               NOT NULL
);
--rollback DROP TABLE booking_request;

--changeset sslepnev:6
CREATE TABLE IF NOT EXISTS review
(
    id          BIGSERIAL PRIMARY KEY,
    hotel_id    INT REFERENCES hotel (id) ON DELETE CASCADE NOT NULL,
    user_id     INT REFERENCES users (id)                   NOT NULL,
    created_at  TIMESTAMP                                   NOT NULL,
    rating      INT                                         NOT NULL,
    description TEXT
);
--rollback DROP TABLE review;

--changeset sslepnev:7
CREATE TABLE IF NOT EXISTS hotel_content
(
    id       SERIAL PRIMARY KEY,
    hotel_id INT REFERENCES hotel (id) ON DELETE CASCADE NOT NULL,
    link     VARCHAR(1024)                               NOT NULL UNIQUE,
    type     VARCHAR(32)                                 NOT NULL
);
--rollback DROP TABLE hotel_content;

--changeset sslepnev:8
CREATE TABLE IF NOT EXISTS room_content
(
    id      SERIAL PRIMARY KEY,
    room_id INT REFERENCES room (id) ON DELETE CASCADE NOT NULL,
    link    VARCHAR(1024)                              NOT NULL UNIQUE,
    type    VARCHAR(32)                                NOT NULL
);
--rollback DROP TABLE room_content;

--changeset sslepnev:9
CREATE TABLE IF NOT EXISTS review_content
(
    id        SERIAL PRIMARY KEY,
    review_id BIGINT REFERENCES review (id) ON DELETE CASCADE NOT NULL,
    link      VARCHAR(1024)                                   NOT NULL UNIQUE,
    type      VARCHAR(32)                                     NOT NULL,
    UNIQUE (review_id, link)
);
--rollback DROP TABLE review_content;