package com.example.rental.service;

import com.example.rental.dto.CarModelDto;
import com.example.rental.entity.CarModel;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.CarModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class CarModelServiceImpl implements CarModelService {
    private final CarModelRepository carModelRepository;

    @Autowired
    public CarModelServiceImpl(CarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
    }

    public CarModelDto createCarModel(CarModelDto carModelDto) {
        CarModel carModel = new CarModel();
        carModel.setBrand(carModelDto.getBrand());
        carModel.setModel(carModelDto.getModel());
        carModel.setYear(carModelDto.getYear());

        CarModel saved = carModelRepository.save(carModel);
        return mapToDto(saved);
    }

    public CarModelDto getCarModelById(Long id) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car model not found"));
        return mapToDto(carModel);
    }

    public CarModelDto updateCarModel(Long id, CarModelDto carModelDto) {
        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car model not found"));

        carModel.setBrand(carModelDto.getBrand());
        carModel.setModel(carModelDto.getModel());
        carModel.setYear(carModelDto.getYear());

        CarModel updated = carModelRepository.save(carModel);
        return mapToDto(updated);
    }

    public void deleteCarModel(Long id) {
        carModelRepository.deleteById(id);
    }

    private CarModelDto mapToDto(CarModel carModel) {
        return CarModelDto.builder()
                .id(carModel.getId())
                .brand(carModel.getBrand())
                .model(carModel.getModel())
                .year(carModel.getYear())
                .build();
    }
}


