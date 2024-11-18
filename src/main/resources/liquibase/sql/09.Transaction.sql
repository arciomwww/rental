CREATE TABLE Transaction (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    amount DECIMAL(10, 2) NOT NULL,
    type VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    application_account_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES Users(id)
    FOREIGN KEY (application_account_id) REFERENCES ApplicationAccount(id)
);