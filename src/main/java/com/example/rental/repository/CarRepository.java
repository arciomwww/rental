package com.example.rental.repository;

import com.example.rental.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByLocationId(Long locationId);
}
