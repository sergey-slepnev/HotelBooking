INSERT INTO users(id, role, username, password, firstname, lastname, birth_date, phone, image, status, registered_at)
VALUES (1, 'ADMIN', 'AdminEmail@gmail.com', 'AdminPassword', 'Sergey', 'Sidorov', '1985-04-22', '8-835-66-99-333',
        'AdminAvatar.jpg', 'APPROVED', '2022-09-08 10:00'),
       (2, 'USER', 'FirstUser@gmail.com', 'FirstUserPassword', 'Natalya', 'Stepanova', '2001-10-22', '+3-958-98-89-000',
        'UserAvatar.jpg', 'NEW', '2022-10-12 11:22'),
       (3, 'USER', 'SecondUser@gmail.com', 'SecondUserPassword', 'Michail', 'Malyshev', '2008-10-22',
        '+3-958-98-89-654', 'UserAvatar.jpg', 'NEW', '2022-10-21 13:10'),
       (4, 'OWNER', 'FirstOwner@gmail.com', 'FirstOwnerPassword', 'Andrey', 'Petrov', '2000-03-15', '+3-958-98-89-555',
        'UserAvatar.jpg', 'NEW', '2022-11-18 10:00'),
       (5, 'OWNER', 'SecondOwner@gmail.com', 'SecondOwnerPassword', 'Jack', 'Ivanov', '2000-03-15', '+3-958-98-89-666',
        'UserAvatar.jpg', 'NEW', '2022-11-15 11:00');
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));

INSERT
INTO hotel(id, owner_id, name, available, status)
VALUES (1, (SELECT id FROM users WHERE username = 'FirstOwner@gmail.com'), 'MoscowPlaza', TRUE, 'APPROVED'),
       (2, (SELECT id FROM users WHERE username = 'FirstOwner@gmail.com'), 'MoscowHotel', TRUE, 'APPROVED'),
       (3, (SELECT id FROM users WHERE username = 'FirstOwner@gmail.com'), 'KievPlaza', TRUE, 'APPROVED'),
       (4, (SELECT id FROM users WHERE username = 'SecondOwner@gmail.com'), 'PiterPlaza', TRUE, 'NEW'),
       (5, (SELECT id FROM users WHERE username = 'SecondOwner@gmail.com'), 'MinskPlaza', TRUE, 'APPROVED');
SELECT SETVAL('hotel_id_seq', (SELECT MAX(id) FROM hotel));

INSERT
INTO hotel_details(id, hotel_id, phone_number, country, locality, area, street, floor_count, star, description)
VALUES (1, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), '1111-111-111', 'Russia', 'Moscow', 'West', 'First', 15,
        'FOUR', 'Very good hotel'),
       (2, (SELECT id FROM hotel WHERE name = 'MoscowHotel'), '0-000-0000-000-00', 'Russia', 'Moscow', 'EastSide',
        'First', 15, 'TWO', 'Not bad hotel'),
       (3, (SELECT id FROM hotel WHERE name = 'KievPlaza'), '2222-22-22', 'Ukraine', 'Kiev', 'West', 'Second', 20,
        'THREE', 'Nice hotel'),
       (4, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), '3333-333-333', 'Russia', 'Saint Petersburg', 'East',
        'Third', 5, 'FIVE', 'The best hotel ever'),
       (5, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), '4444-44-44', 'Belarus', 'Minsk', 'North', 'Fourth', 10,
        'TWO', 'Really good hotel!');
SELECT SETVAL('hotel_details_id_seq', (SELECT MAX(id) FROM hotel_details));

INSERT INTO hotel_content (id, hotel_id, link, type)
VALUES (1, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 'moscowPlaza.jpg', 'PHOTO'),
       (2, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 'moscowPlaza.MP4', 'VIDEO'),
       (3, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 'minskPlaza.jpg', 'PHOTO'),
       (4, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 'minskPlaza.MP4', 'VIDEO'),
       (5, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 'kievPlaza.jpg', 'PHOTO'),
       (6, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 'kievPlaza.MP4', 'VIDEO'),
       (7, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 'piterPlaza.jpg', 'PHOTO'),
       (8, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 'piterPlaza.MP4', 'VIDEO');
SELECT SETVAL('hotel_content_id_seq', (SELECT MAX(id) FROM hotel_content));

INSERT INTO room(id, hotel_id, room_no, type, square, adult_bed_count, children_bed_count, cost, available, floor,
                 description)
VALUES (1, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 1, 'TRPL', 25.3, 3, 0, 1500.99, TRUE, 1,
        'Nice room in moscowPlaza'),
       (2, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 2, 'QDPL', 45.0, 4, 2, 2500, TRUE, 1,
        'Nice room in moscowPlaza'),
       (3, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 7, 'TWIN', 35.5, 2, 1, 1900.85, TRUE, 3,
        'Nice room in moscowPlaza'),
       (4, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 7, 'SGL', 20.5, 1, 0, 850.58, FALSE, 5,
        'Nice room in moscowPlaza'),
       (5, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 3, 'TRPL', 55.5, 3, 1, 1700, TRUE, 1,
        'Nice room in kievPlaza'),
       (6, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 4, 'QDPL', 15.0, 4, 2, 2900, TRUE, 1,
        'Nice room in kievPlaza'),
       (7, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 6, 'TWIN', 35.5, 2, 0, 1950.58, TRUE, 3,
        'Nice room in kievPlaza'),
       (8, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 12, 'SGL', 23.5, 1, 0, 950.58, FALSE, 5,
        'Nice room in kievPlaza'),
       (9, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 3, 'TRPL', 25.5, 3, 2, 1500, TRUE, 1,
        'Nice room in piterPlaza'),
       (10, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 1, 'QDPL', 45.0, 4, 1, 2500, TRUE, 1,
        'Nice room in piterPlaza'),
       (11, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 6, 'TWIN', 35.5, 2, 1, 1900.85, TRUE, 3,
        'Nice room in piterPlaza'),
       (12, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 5, 'SGL', 20.5, 1, 0, 850.58, FALSE, 5,
        'Nice room in piterPlaza'),
       (13, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 1, 'TRPL', 25.5, 3, 1, 1900, TRUE, 1,
        'Nice room in minskPlaza'),
       (14, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 1, 'QDPL', 45.0, 4, 2, 2100, TRUE, 1,
        'Nice room in minskPlaza'),
       (15, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 7, 'TWIN', 35.5, 2, 1, 1100.85, TRUE, 3,
        'Nice room in minskPlaza'),
       (16, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 10, 'SGL', 20.5, 1, 0, 1850.58, FALSE, 5,
        'Nice room in minskPlaza');
SELECT SETVAL('room_id_seq', (SELECT MAX(id) FROM room));

INSERT INTO room_content(id, room_id, link, type)
VALUES (1, 1, 'first_room_1.jpg', 'PHOTO'),
       (2, 1, 'first_room_2.jpg', 'PHOTO'),
       (3, 1, 'first_room_3.jpg', 'PHOTO'),
       (4, 1, 'first_room_4.jpg', 'PHOTO');
SELECT SETVAL('room_content_id_seq', (SELECT MAX(id) FROM room_content));

INSERT INTO booking_request(id, created_at, hotel_id, room_id, user_id, check_in, check_out, status)
VALUES (1, '2022-10-10 12:05', (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 1, 2, '2022-10-10', '2022-10-15',
        'NEW'),
       (2, '2022-10-11 12:05', (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 6, 2, '2022-11-10', '2022-11-15',
        'NEW'),
       (3, '2022-10-12 12:05', (SELECT id FROM hotel WHERE name = 'KievPlaza'), 3, 2, '2022-10-22', '2022-11-30',
        'APPROVED'),
       (4, '2022-10-13 12:05', (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 1, 2, '2022-12-10', '2022-12-15',
        'APPROVED'),
       (5, '2022-10-14 12:05', (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 10, 2, '2023-01-10', '2023-12-15',
        'PAID'),
       (6, '2022-10-15 12:05', (SELECT id FROM hotel WHERE name = 'KievPlaza'), 4, 3, '2022-10-10', '2022-10-15',
        'CANCELED'),
       (7, '2022-10-16 12:05', (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 6, 3, '2022-12-10', '2022-12-15',
        'APPROVED'),
       (8, '2022-10-17 12:05', (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 2, 3, '2023-01-10', '2023-01-15',
        'PAID'),
       (9, '2022-10-18 12:05', (SELECT id FROM hotel WHERE name = 'KievPlaza'), 12, 3, '2022-10-10', '2022-01-15',
        'CANCELED');
SELECT SETVAL('booking_request_id_seq', (SELECT MAX(id) FROM booking_request));

INSERT INTO review(id, hotel_id, user_id, created_at, rating)
VALUES (1, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 2, NOW(), 5),
       (2, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 3, NOW(), 2),
       (3, (SELECT id FROM hotel WHERE name = 'MinskPlaza'), 3, NOW(), 4),
       (4, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 2, NOW(), 4),
       (5, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 2, NOW(), 3),
       (6, (SELECT id FROM hotel WHERE name = 'KievPlaza'), 2, NOW(), 3),
       (7, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 2, NOW(), 5),
       (8, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 2, NOW(), 3),
       (9, (SELECT id FROM hotel WHERE name = 'MoscowPlaza'), 2, NOW(), 5),
       (10, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 2, NOW(), 4),
       (11, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 2, NOW(), 4),
       (12, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 2, NOW(), 3),
       (13, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 2, NOW(), 5),
       (14, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 2, NOW(), 4),
       (15, (SELECT id FROM hotel WHERE name = 'PiterPlaza'), 2, NOW(), 3),
       (16, (SELECT id FROM hotel WHERE name = 'MoscowHotel'), 2, NOW(), 5),
       (17, (SELECT id FROM hotel WHERE name = 'MoscowHotel'), 2, NOW(), 4),
       (18, (SELECT id FROM hotel WHERE name = 'MoscowHotel'), 2, NOW(), 3),
       (19, (SELECT id FROM hotel WHERE name = 'MoscowHotel'), 2, NOW(), 3),
       (20, (SELECT id FROM hotel WHERE name = 'MoscowHotel'), 2, NOW(), 5);
SELECT SETVAL('review_id_seq', (SELECT MAX(id) FROM review));

INSERT INTO review_content(id, review_id, link, type)
VALUES (1, 1, 'Photo_1.jpg', 'PHOTO'),
       (2, 2, 'Video_1.jpg', 'VIDEO'),
       (3, 4, 'Video_2.jpg', 'PHOTO');
SELECT SETVAL('review_content_id_seq', (SELECT MAX(id) FROM review_content));