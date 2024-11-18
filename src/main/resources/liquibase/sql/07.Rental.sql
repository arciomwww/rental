CREATE TABLE Rental (
    id SERIAL PRIMARY KEY,
    car_id INTEGER,
    user_id INTEGER,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    mileage INT,
    additional_info TEXT,
    FOREIGN KEY (car_id) REFERENCES Car(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);