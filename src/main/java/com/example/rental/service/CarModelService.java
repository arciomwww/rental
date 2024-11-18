package com.example.rental.service;

import com.example.rental.dto.CarModelDto;

public interface CarModelService {
    CarModelDto createCarModel(CarModelDto carModelDto);
    CarModelDto getCarModelById(Long id);
    CarModelDto updateCarModel(Long id, CarModelDto carModelDto);
    void deleteCarModel(Long id);
}
