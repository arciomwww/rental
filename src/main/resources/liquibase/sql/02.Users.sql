CREATE TABLE Users (
    id         SERIAL        PRIMARY KEY,
    username   VARCHAR(64)   NOT NULL UNIQUE,
    password   VARCHAR(2048) NOT NULL,
    email      VARCHAR(64)   NOT NULL UNIQUE,
    first_name VARCHAR(64)   NOT NULL,
    last_name  VARCHAR(64)   NOT NULL,
    enabled    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    update_at  TIMESTAMP
);