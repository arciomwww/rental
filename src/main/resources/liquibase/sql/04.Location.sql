CREATE TABLE Location (
    id      SERIAL PRIMARY KEY,
    parking_lot    VARCHAR(64) NOT NULL,
    address        VARCHAR(64) NOT NULL,
    city           VARCHAR(64) NOT NULL,
    country        VARCHAR(64) NOT NULL
);