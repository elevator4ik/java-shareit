CREATE TABLE IF NOT EXISTS users
(
    user_id   integer      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_name varchar(255) NOT NULL,
    email     varchar(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description  varchar(1024),
    requester_id integer
        CONSTRAINT FK_REQUESTS_U REFERENCES users (user_id),
    create_date  timestamp
);

CREATE TABLE IF NOT EXISTS items
(
    item_id          integer       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item_name        varchar(255)  NOT NULL,
    item_description varchar(1024) NOT NULL,
    is_available     boolean       NOT NULL,
    request_id       integer
        CONSTRAINT FK_ITEMS_R REFERENCES requests (request_id),
    owner_id         integer
        CONSTRAINT FK_ITEMS_U REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    start_date timestamp,
    end_date   timestamp,
    item_id    integer
        CONSTRAINT FK_BOOKINGS_I REFERENCES items (item_id),
    booker_id  integer
        CONSTRAINT FK_BOOKINGS_U REFERENCES users (user_id),
    status     varchar
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id   integer NOT NULL AUTO_INCREMENT PRIMARY KEY,
    comment_text varchar(2048),
    create_date  timestamp,
    author_id    integer
        CONSTRAINT FK_COMMENTS_U REFERENCES users (user_id),
    item_id      integer
        CONSTRAINT FK_COMMENTS_I REFERENCES items (item_id)
);