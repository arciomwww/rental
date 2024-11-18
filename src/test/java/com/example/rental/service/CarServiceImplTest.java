package com.example.rental.service;

import com.example.rental.dto.CarDto;
import com.example.rental.entity.Car;
import com.example.rental.entity.CarModel;
import com.example.rental.entity.Location;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.CarRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImplTest.class);
    private AutoCloseable mocks;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        logger.info("Запуск тестов для CarServiceImpl...");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
        logger.info("Завершение тестов для CarServiceImpl...");
    }

    @Test
    void createCar_ShouldCreateAndReturnCarDto() {
        logger.info("Запуск теста createCar_ShouldCreateAndReturnCarDto...");

        CarDto carDto = CarDto.builder()
                .modelId(1L)
                .locationId(1L)
                .status("Available")
                .pricePerHour(BigDecimal.valueOf(20))
                .pricePerDay(BigDecimal.valueOf(100))
                .build();

        Car car = new Car();
        car.setId(1L);
        car.setModel(new CarModel(1L));
        car.setLocation(new Location(1L));
        car.setStatus("Available");
        car.setPricePerHour(BigDecimal.valueOf(20));
        car.setPricePerDay(BigDecimal.valueOf(100));

        when(carRepository.save(any(Car.class))).thenReturn(car);
        logger.info("Настроен мок для сохранения автомобиля: {}", car);

        CarDto result = carService.createCar(carDto);

        assertNotNull(result);
        assertEquals(1L, result.getModelId());
        assertEquals(1L, result.getLocationId());
        assertEquals("Available", result.getStatus());
        assertEquals(BigDecimal.valueOf(20), result.getPricePerHour());
        assertEquals(BigDecimal.valueOf(100), result.getPricePerDay());
        logger.info("Созданный CarDto: {}", result);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void getAllCars_ShouldReturnPagedCarDtos() {
        logger.info("Запуск теста getAllCars_ShouldReturnPagedCarDtos...");

        Pageable pageable = PageRequest.of(0, 10);
        Car car = new Car();
        car.setId(1L);
        car.setModel(new CarModel(1L));
        car.setLocation(new Location(1L));
        car.setStatus("Available");
        car.setPricePerHour(BigDecimal.valueOf(20));
        car.setPricePerDay(BigDecimal.valueOf(100));

        Page<Car> carPage = new PageImpl<>(Collections.singletonList(car), pageable, 1);
        when(carRepository.findAll(pageable)).thenReturn(carPage);
        logger.info("Настроен мок для поиска всех автомобилей: {}", carPage);

        Page<CarDto> result = carService.getAllCars(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Available", result.getContent().get(0).getStatus());
        logger.info("Результат получения автомобилей: {}", result);
        verify(carRepository, times(1)).findAll(pageable);
    }

    @Test
    void getCarById_ShouldReturnCarDto_WhenCarExists() {
        logger.info("Запуск теста getCarById_ShouldReturnCarDto_WhenCarExists...");

        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);
        car.setModel(new CarModel(1L));
        car.setLocation(new Location(1L));
        car.setStatus("Available");
        car.setPricePerHour(BigDecimal.valueOf(20));
        car.setPricePerDay(BigDecimal.valueOf(100));

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        logger.info("Настроен мок для поиска автомобиля с ID: {}", carId);

        CarDto result = carService.getCarById(carId);

        assertNotNull(result);
        assertEquals(1L, result.getModelId());
        assertEquals("Available", result.getStatus());
        logger.info("Полученный CarDto: {}", result);
        verify(carRepository, times(1)).findById(carId);
    }

    @Test
    void getCarById_ShouldThrowException_WhenCarNotFound() {
        logger.info("Запуск теста getCarById_ShouldThrowException_WhenCarNotFound...");

        Long carId = 1L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        logger.info("Настроен мок для отсутствия автомобиля с ID: {}", carId);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> carService.getCarById(carId));
        logger.error("Исключение ResourceNotFoundException успешно выброшено: {}", exception.getMessage());
    }

    @Test
    void updateCar_ShouldUpdateAndReturnUpdatedCarDto() {
        logger.info("Запуск теста updateCar_ShouldUpdateAndReturnUpdatedCarDto...");

        Long carId = 1L;
        CarDto carDto = CarDto.builder()
                .modelId(1L)
                .locationId(1L)
                .status("Rented")
                .pricePerHour(BigDecimal.valueOf(30))
                .pricePerDay(BigDecimal.valueOf(120))
                .build();

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setModel(new CarModel(1L));
        existingCar.setLocation(new Location(1L));
        existingCar.setStatus("Available");
        existingCar.setPricePerHour(BigDecimal.valueOf(20));
        existingCar.setPricePerDay(BigDecimal.valueOf(100));

        Car updatedCar = new Car();
        updatedCar.setId(carId);
        updatedCar.setModel(new CarModel(1L));
        updatedCar.setLocation(new Location(1L));
        updatedCar.setStatus("Rented");
        updatedCar.setPricePerHour(BigDecimal.valueOf(30));
        updatedCar.setPricePerDay(BigDecimal.valueOf(120));

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);
        logger.info("Настроены моки для обновления автомобиля с ID: {}", carId);

        CarDto result = carService.updateCar(carId, carDto);

        assertNotNull(result);
        assertEquals("Rented", result.getStatus());
        assertEquals(BigDecimal.valueOf(30), result.getPricePerHour());
        logger.info("Обновленный CarDto: {}", result);
        verify(carRepository, times(1)).save(existingCar);
    }

    @Test
    void updateCar_ShouldThrowException_WhenCarNotFound() {
        logger.info("Запуск теста updateCar_ShouldThrowException_WhenCarNotFound...");

        Long carId = 1L;
        CarDto carDto = CarDto.builder()
                .modelId(1L)
                .locationId(1L)
                .status("Rented")
                .pricePerHour(BigDecimal.valueOf(30))
                .pricePerDay(BigDecimal.valueOf(120))
                .build();

        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        logger.info("Настроен мок для отсутствия автомобиля с ID: {}", carId);

        assertThrows(ResourceNotFoundException.class, () -> carService.updateCar(carId, carDto));
        logger.error("Исключение ResourceNotFoundException успешно выброшено.");
    }

    @Test
    void deleteCar_ShouldDeleteCar_WhenCarExists() {
        logger.info("Запуск теста deleteCar_ShouldDeleteCar_WhenCarExists...");

        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        doNothing().when(carRepository).delete(car);
        logger.info("Настроен мок для удаления автомобиля с ID: {}", carId);

        carService.deleteCar(carId);

        logger.info("Автомобиль с ID: {} успешно удален.", carId);
        verify(carRepository, times(1)).delete(car);
    }
}
