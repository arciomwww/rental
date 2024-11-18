CREATE TABLE Account (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);