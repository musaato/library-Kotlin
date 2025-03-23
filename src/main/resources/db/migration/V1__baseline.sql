-- 1 Create the table using the sequence
DROP SEQUENCE IF EXISTS book_id_seq CASCADE;
CREATE SEQUENCE book_id_seq START WITH 1000000 INCREMENT BY 50;

DROP TABLE IF EXISTS book;
CREATE TABLE book
(
    id                 bigint PRIMARY KEY      DEFAULT nextval('book_id_seq'),
    isbn               VARCHAR(13)    NOT NULL,
    title              varchar(128)   NOT NULL,
    price              decimal(10, 2) NOT NULL CHECK (Price >= 0),
    publication_status varchar(11)    NOT NULL DEFAULT 'published' CHECK (publication_status IN ('unpublished', 'published')),
    book_cover         varchar(128)            DEFAULT NULL,
    description        varchar(2048)           DEFAULT NULL,
    publication_date   timestamp               DEFAULT NULL,
    created_time       timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time       timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create the Trigger Function to monitor publication status update statement
CREATE OR REPLACE FUNCTION check_publication_status_change()
    RETURNS TRIGGER AS
$$
BEGIN
    IF OLD.publication_status = 'published' AND NEW.publication_status = 'unpublished' THEN
        RAISE EXCEPTION 'Cannot change publication_status from published to unpublished';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

INSERT INTO book (isbn, title, price, publication_status, book_cover, description, publication_date, created_time,
                  updated_time)
VALUES ('9781234567890', 'The Great Adventure', 29.99, 'published', 'http://example.com/cover1.jpg',
        'An exciting journey of discovery.', '2023-01-15 10:00:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567891', 'Mystery of the Old House', 19.99, 'unpublished', 'http://example.com/cover2.jpg',
        'A gripping mystery novel.', '2023-03-22 14:30:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567892', 'Science for Beginners', 39.99, 'published', 'http://example.com/cover3.jpg',
        'An introductory guide to science.', '2024-06-10 09:45:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567893', 'History Unveiled', 24.99, 'published', 'http://example.com/cover4.jpg',
        'Exploring historical events.', '2023-09-30 11:20:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567894', 'Cooking with Passion', 34.99, 'unpublished', 'http://example.com/cover5.jpg',
        'Delicious recipes from around the world.', '2024-02-14 16:15:00', '2023-12-04 21:30:00',
        '2023-12-04 21:30:00'),
       ('9781234567895', 'Tech Innovations', 44.99, 'published', 'http://example.com/cover6.jpg',
        'Latest advancements in technology.', '2023-07-04 10:50:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567896', 'Healthy Living', 22.99, 'published', 'http://example.com/cover7.jpg',
        'Tips for a healthier lifestyle.', '2023-05-25 13:35:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567897', 'Fantasy Worlds', 18.99, 'published', 'http://example.com/cover8.jpg',
        'Enter the realm of fantasy.', '2023-11-11 08:20:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567898', 'Art and Creativity', 27.99, 'unpublished', 'http://example.com/cover9.jpg',
        'Unleash your artistic potential.', '2023-08-17 15:45:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00'),
       ('9781234567899', 'Travel the World', 49.99, 'published', 'http://example.com/cover10.jpg',
        'A travel guide to the best destinations.', '2024-01-01 12:00:00', '2023-12-04 21:30:00', '2023-12-04 21:30:00')
;

-- create author table
DROP SEQUENCE IF EXISTS author_id_seq CASCADE;
CREATE SEQUENCE author_id_seq START WITH 1000001 INCREMENT BY 50;

DROP TABLE IF EXISTS author;
CREATE TABLE author
(
    id               bigint PRIMARY KEY    DEFAULT nextval('author_id_seq'),
    first_name       varchar(128) NOT NULL,
    last_name        varchar(128) NOT NULL,
    profile_picture  varchar(128)          DEFAULT NULL,
    biography        varchar(2048)         DEFAULT NULL,
    nationality_code varchar(3)            DEFAULT NULL,
    birthdate        timestamp             DEFAULT NULL CHECK (birthdate < CURRENT_TIMESTAMP),
    created_time     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- insert dummy data to author table
INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('John', 'Doe', '1980-01-15', '2014-06-15 14:25:10', '2020-03-11 08:12:45');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Jane', 'Smith', '1985-02-20', '2012-09-10 09:30:20', '2019-08-23 16:47:50');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Alice', 'Johnson', '1978-03-05', '2016-02-18 11:45:30', '2021-11-05 07:19:35');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Bob', 'Brown', '1990-04-10', '2017-08-23 13:10:40', '2022-04-22 10:39:20');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Charlie', 'Davis', '1982-05-25', '2013-03-12 15:55:50', '2018-09-16 12:26:40');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Dana', 'Miller', '1988-06-30', '2015-07-05 18:20:10', '2020-12-01 09:53:55');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Eve', 'Wilson', '1975-07-15', '2011-11-22 08:35:15', '2019-06-27 14:41:25');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Frank', 'Taylor', '1983-08-20', '2014-05-13 20:50:25', '2021-02-09 17:05:50');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Grace', 'Anderson', '1979-09-10', '2018-01-09 22:15:35', '2023-04-19 06:28:30');

INSERT INTO author (first_name, last_name, birthdate, created_time, updated_time)
VALUES ('Hank', 'Thomas', '1984-10-05', '2016-10-29 11:30:45', '2022-08-15 05:59:40');


-- create book_authors table
DROP TABLE IF EXISTS book_authors;
CREATE TABLE book_authors
(
    book_id      bigint REFERENCES book (id),
    author_id    bigint REFERENCES author (id),
    role         varchar(50)        DEFAULT NULL,
    contribution varchar(255)       DEFAULT NULL,
    created_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (book_id, author_id)
);
-- John Doe
INSERT INTO book_authors (book_id, author_id, role, contribution, created_time, updated_time)
VALUES (1000500, 1000001, 'Main Author', 'Wrote the entire book', '2014-06-15 14:25:10', '2020-03-11 08:12:45'),
       (1000550, 1000001, 'Main Author', 'Wrote the entire book', '2012-09-10 09:30:20', '2019-08-23 16:47:50');

-- Jane Smith
INSERT INTO book_authors (book_id, author_id, role, contribution, created_time, updated_time)
VALUES (1000600, 1000051, 'Main Author', 'Wrote the entire book', '2016-02-18 11:45:30', '2021-11-05 07:19:35'),
       (1000650, 1000051, 'Main Author', 'Wrote the entire book', '2017-08-23 13:10:40', '2022-04-22 10:39:20');

-- Alice Johnson
INSERT INTO book_authors (book_id, author_id, role, contribution, created_time, updated_time)
VALUES (1000700, 1000101, 'Main Author', 'Wrote the entire book', '2013-03-12 15:55:50', '2018-09-16 12:26:40'),
       (1000750, 1000101, 'Main Author', 'Wrote the entire book', '2015-07-05 18:20:10', '2020-12-01 09:53:55');

-- Bob Brown
INSERT INTO book_authors (book_id, author_id, role, contribution, created_time, updated_time)
VALUES (1000800, 1000151, 'Main Author', 'Wrote the entire book', '2011-11-22 08:35:15', '2019-06-27 14:41:25'),
       (1000850, 1000151, 'Main Author', 'Wrote the entire book', '2014-05-13 20:50:25', '2021-02-09 17:05:50'),
       (1000900, 1000151, 'Main Author', 'Wrote the majority book', '2018-01-09 22:15:35', '2023-04-19 06:28:30');
-- Charlie Davis
INSERT INTO book_authors (book_id, author_id, role, contribution, created_time, updated_time)
VALUES (1000900, 1000201, 'Support Author', 'Wrote the partial book', '2016-10-29 11:30:45', '2022-08-15 05:59:40');