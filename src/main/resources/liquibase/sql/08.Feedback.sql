CREATE TABLE Feedback (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    car_id INTEGER,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 10),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (car_id) REFERENCES Car(id)
);