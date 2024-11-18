CREATE TABLE Car (
    id SERIAL PRIMARY KEY,
    model_id INTEGER,
    location_id INTEGER,
    status VARCHAR(64) NOT NULL,
    price_per_hour DECIMAL(10, 2) NOT NULL,
    price_per_day DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (model_id) REFERENCES CarModel(id),
    FOREIGN KEY (location_id) REFERENCES Location(id)
);