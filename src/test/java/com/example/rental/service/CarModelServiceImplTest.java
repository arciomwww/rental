package com.example.rental.service;

import com.example.rental.dto.CarModelDto;
import com.example.rental.entity.CarModel;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.CarModelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarModelServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CarModelServiceImplTest.class);
    private AutoCloseable closeable;

    @Mock
    private CarModelRepository carModelRepository;

    @InjectMocks
    private CarModelServiceImpl carModelService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        logger.info("Настройка теста: {}", this.getClass().getSimpleName());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        logger.info("Очистка после теста: {}", this.getClass().getSimpleName());
    }

    @Test
    void createCarModel_ShouldCreateAndReturnCarModelDto() {
        logger.info("Запуск теста: createCarModel_ShouldCreateAndReturnCarModelDto");

        CarModelDto carModelDto = CarModelDto.builder()
                .brand("Toyota")
                .model("Camry")
                .year(2022)
                .build();

        CarModel carModel = new CarModel();
        carModel.setBrand(carModelDto.getBrand());
        carModel.setModel(carModelDto.getModel());
        carModel.setYear(carModelDto.getYear());
        carModel.setId(1L);

        when(carModelRepository.save(any(CarModel.class))).thenReturn(carModel);
        logger.info("Сохранение модели автомобиля: {}", carModelDto);

        CarModelDto result = carModelService.createCarModel(carModelDto);
        logger.info("Результат создания модели автомобиля: {}", result);

        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        assertEquals("Camry", result.getModel());
        assertEquals(2022, result.getYear());
        verify(carModelRepository, times(1)).save(any(CarModel.class));
    }

    @Test
    void getCarModelById_ShouldReturnCarModelDto_WhenCarModelExists() {
        logger.info("Запуск теста: getCarModelById_ShouldReturnCarModelDto_WhenCarModelExists");

        Long carModelId = 1L;
        CarModel carModel = new CarModel();
        carModel.setId(carModelId);
        carModel.setBrand("Honda");
        carModel.setModel("Civic");
        carModel.setYear(2021);

        when(carModelRepository.findById(carModelId)).thenReturn(Optional.of(carModel));
        logger.info("Получение модели автомобиля по ID: {}", carModelId);

        CarModelDto result = carModelService.getCarModelById(carModelId);
        logger.info("Результат получения модели автомобиля: {}", result);

        assertNotNull(result);
        assertEquals("Honda", result.getBrand());
        assertEquals("Civic", result.getModel());
        assertEquals(2021, result.getYear());
    }

    @Test
    void getCarModelById_ShouldThrowException_WhenCarModelNotFound() {
        logger.info("Запуск теста: getCarModelById_ShouldThrowException_WhenCarModelNotFound");

        Long carModelId = 1L;
        when(carModelRepository.findById(carModelId)).thenReturn(Optional.empty());
        logger.info("Попытка получить модель автомобиля, которая не существует: {}", carModelId);

        assertThrows(ResourceNotFoundException.class, () -> carModelService.getCarModelById(carModelId));
        logger.info("Исключение было выброшено, как и ожидалось.");
    }

    @Test
    void updateCarModel_ShouldUpdateAndReturnUpdatedCarModelDto() {
        logger.info("Запуск теста: updateCarModel_ShouldUpdateAndReturnUpdatedCarModelDto");

        Long carModelId = 1L;
        CarModel existingCarModel = new CarModel();
        existingCarModel.setId(carModelId);
        existingCarModel.setBrand("BMW");
        existingCarModel.setModel("X5");
        existingCarModel.setYear(2020);

        CarModelDto updateDto = CarModelDto.builder()
                .brand("BMW")
                .model("X6")
                .year(2021)
                .build();

        CarModel updatedCarModel = new CarModel();
        updatedCarModel.setId(carModelId);
        updatedCarModel.setBrand("BMW");
        updatedCarModel.setModel("X6");
        updatedCarModel.setYear(2021);

        when(carModelRepository.findById(carModelId)).thenReturn(Optional.of(existingCarModel));
        when(carModelRepository.save(any(CarModel.class))).thenReturn(updatedCarModel);
        logger.info("Обновление модели автомобиля с ID: {}", carModelId);

        CarModelDto result = carModelService.updateCarModel(carModelId, updateDto);
        logger.info("Результат обновления модели автомобиля: {}", result);

        assertNotNull(result);
        assertEquals("BMW", result.getBrand());
        assertEquals("X6", result.getModel());
        assertEquals(2021, result.getYear());
        verify(carModelRepository, times(1)).save(existingCarModel);
    }

    @Test
    void updateCarModel_ShouldThrowException_WhenCarModelNotFound() {
        logger.info("Запуск теста: updateCarModel_ShouldThrowException_WhenCarModelNotFound");

        Long carModelId = 1L;
        CarModelDto updateDto = CarModelDto.builder()
                .brand("Ford")
                .model("Mustang")
                .year(2023)
                .build();

        when(carModelRepository.findById(carModelId)).thenReturn(Optional.empty());
        logger.info("Попытка обновить модель автомобиля, которая не существует: {}", carModelId);

        assertThrows(ResourceNotFoundException.class, () -> carModelService.updateCarModel(carModelId, updateDto));
        logger.info("Исключение было выброшено, как и ожидалось.");
    }

    @Test
    void deleteCarModel_ShouldDeleteCarModel_WhenCarModelExists() {
        logger.info("Запуск теста: deleteCarModel_ShouldDeleteCarModel_WhenCarModelExists");

        Long carModelId = 1L;
        doNothing().when(carModelRepository).deleteById(carModelId);
        logger.info("Удаление модели автомобиля с ID: {}", carModelId);

        carModelService.deleteCarModel(carModelId);
        logger.info("Модель автомобиля с ID: {} была успешно удалена.", carModelId);

        verify(carModelRepository, times(1)).deleteById(carModelId);
    }
}
