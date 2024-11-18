package com.example.rental.service;

import com.example.rental.dto.CarDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto createCar(CarDto carDto);
    CarDto getCarById(Long id);
    CarDto updateCar(Long id, CarDto carDto);
    void deleteCar(Long id);

    Page<CarDto> getAllCars(Pageable pageable);
}
