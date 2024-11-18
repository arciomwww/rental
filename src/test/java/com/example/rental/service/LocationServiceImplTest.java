package com.example.rental.service;

import com.example.rental.dto.LocationDto;
import com.example.rental.entity.Car;
import com.example.rental.entity.CarModel;
import com.example.rental.entity.Location;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.CarRepository;
import com.example.rental.repository.LocationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImplTest.class);
    private AutoCloseable mocks;

    @Mock
    private CarRepository carRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        logger.info("Мок-объекты инициализированы для тестов LocationServiceImpl.");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
            logger.info("Мок-объекты очищены после теста.");
        }
    }

    @Test
    void createLocation_ShouldReturnCreatedLocationDto() {
        logger.info("Начало теста: createLocation_ShouldReturnCreatedLocationDto");

        Location location = new Location();
        location.setId(1L);
        location.setParkingLot("A1");
        location.setAddress("123 Main St");
        location.setCity("City");
        location.setCountry("Country");

        logger.debug("Создание мок-локации: {}", location);

        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationDto locationDto = new LocationDto();
        locationDto.setParkingLot("A1");
        locationDto.setAddress("123 Main St");
        locationDto.setCity("City");
        locationDto.setCountry("Country");

        logger.debug("Создание LocationDto: {}", locationDto);

        LocationDto createdLocationDto = locationService.createLocation(locationDto);

        assertNotNull(createdLocationDto);
        assertEquals("A1", createdLocationDto.getParkingLot());
        logger.debug("Созданное LocationDto: {}", createdLocationDto);

        verify(locationRepository, times(1)).save(any(Location.class));
        logger.info("Тест прошёл: createLocation_ShouldReturnCreatedLocationDto");
    }

    @Test
    void getAllLocations_ShouldReturnPagedLocations() {
        logger.info("Начало теста: getAllLocations_ShouldReturnPagedLocations");

        Location location = new Location();
        location.setId(1L);
        Page<Location> locationPage = new PageImpl<>(Collections.singletonList(location));

        logger.debug("Создание мок-страницы с локацией: {}", location);

        when(locationRepository.findAll(any(PageRequest.class))).thenReturn(locationPage);

        Page<LocationDto> result = locationService.getAllLocations(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        logger.debug("Полученная страница с общим количеством элементов: {}", result.getTotalElements());

        verify(locationRepository, times(1)).findAll(any(PageRequest.class));
        logger.info("Тест прошёл: getAllLocations_ShouldReturnPagedLocations");
    }

    @Test
    void getLocationById_ExistingId_ShouldReturnLocationDto() {
        logger.info("Начало теста: getLocationById_ExistingId_ShouldReturnLocationDto");

        Location location = new Location();
        location.setId(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        logger.debug("Мокирование получения локации для ID: {}", 1L);

        LocationDto locationDto = locationService.getLocationById(1L);

        assertNotNull(locationDto);
        assertEquals(1L, locationDto.getId());
        logger.debug("Полученное LocationDto: {}", locationDto);

        verify(locationRepository, times(1)).findById(1L);
        logger.info("Тест прошёл: getLocationById_ExistingId_ShouldReturnLocationDto");
    }

    @Test
    void getLocationById_NonExistingId_ShouldThrowResourceNotFoundException() {
        logger.info("Начало теста: getLocationById_NonExistingId_ShouldThrowResourceNotFoundException");

        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        logger.debug("Мокирование получения для несуществующего ID локации: {}", 1L);

        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationById(1L));
        logger.debug("Ожидаемое исключение ResourceNotFoundException выброшено для ID: {}", 1L);

        verify(locationRepository, times(1)).findById(1L);
        logger.info("Тест прошёл: getLocationById_NonExistingId_ShouldThrowResourceNotFoundException");
    }

    @Test
    void updateLocation_ShouldReturnUpdatedLocationDto() {
        logger.info("Начало теста: updateLocation_ShouldReturnUpdatedLocationDto");

        Location location = new Location();
        location.setId(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        logger.debug("Мокирование получения и обновления для ID локации: {}", 1L);

        LocationDto locationDto = new LocationDto();
        locationDto.setParkingLot("A1 Updated");
        locationDto.setAddress("Updated Address");

        logger.debug("Обновление LocationDto: {}", locationDto);

        LocationDto updatedLocationDto = locationService.updateLocation(1L, locationDto);

        assertEquals("A1 Updated", updatedLocationDto.getParkingLot());
        logger.debug("Обновлённое LocationDto: {}", updatedLocationDto);

        verify(locationRepository, times(1)).save(any(Location.class));
        logger.info("Тест прошёл: updateLocation_ShouldReturnUpdatedLocationDto");
    }

    @Test
    void deleteLocation_ExistingId_ShouldDeleteLocation() {
        logger.info("Начало теста: deleteLocation_ExistingId_ShouldDeleteLocation");

        Location location = new Location();
        location.setId(1L);
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        logger.debug("Мокирование удаления для ID локации: {}", 1L);

        locationService.deleteLocation(1L);

        verify(locationRepository, times(1)).delete(location);
        logger.info("Тест прошёл: deleteLocation_ExistingId_ShouldDeleteLocation");
    }

    @Test
    void getLocationsByCity_ShouldReturnLocationDtos() {
        logger.info("Начало теста: getLocationsByCity_ShouldReturnLocationDtos");

        Location location = new Location();
        location.setCity("City");
        when(locationRepository.findByCity("City")).thenReturn(List.of(location));

        logger.debug("Мокирование получения локаций по городу: {}", "City");

        List<LocationDto> result = locationService.getLocationsByCity("City");

        assertNotNull(result);
        assertEquals(1, result.size());
        logger.debug("Полученные LocationDtos: {}", result);

        verify(locationRepository, times(1)).findByCity("City");
        logger.info("Тест прошёл: getLocationsByCity_ShouldReturnLocationDtos");
    }

    @Test
    void getLocationsByCountry_ShouldReturnLocationDtos() {
        logger.info("Начало теста: getLocationsByCountry_ShouldReturnLocationDtos");

        Location location = new Location();
        location.setCountry("Country");
        when(locationRepository.findByCountry("Country")).thenReturn(List.of(location));

        logger.debug("Мокирование получения локаций по стране: {}", "Country");

        List<LocationDto> result = locationService.getLocationsByCountry("Country");

        assertNotNull(result);
        assertEquals(1, result.size());
        logger.debug("Полученные LocationDtos: {}", result);

        verify(locationRepository, times(1)).findByCountry("Country");
        logger.info("Тест прошёл: getLocationsByCountry_ShouldReturnLocationDtos");
    }

    @Test
    void getLocationByCarId_ShouldReturnLocationDtoWithCars() {
        logger.info("Начало теста: getLocationByCarId_ShouldReturnLocationDtoWithCars");

        // Устанавливаем локацию
        Location location = new Location();
        location.setId(1L);

        // Устанавливаем модель автомобиля
        CarModel carModel = new CarModel();
        carModel.setId(1L);
        carModel.setBrand("Toyota");
        carModel.setModel("Camry");

        // Устанавливаем автомобиль и связываем его с моделью автомобиля
        Car car = new Car();
        car.setId(1L);
        car.setModel(carModel);
        car.setLocation(location);

        logger.debug("Создание мок-автомобиля: {}", car);

        // Мокируем поведение репозитория
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(carRepository.findByLocationId(1L)).thenReturn(List.of(car));

        logger.debug("Мокирование получения локации для ID: {}", 1L);

        // Получаем DTO
        LocationDto locationDto = locationService.getLocationByCarId(1L);

        assertNotNull(locationDto);
        assertEquals(1L, locationDto.getId());
        assertEquals(1, locationDto.getCars().size());
        assertEquals(1L, locationDto.getCars().get(0).getId());
        logger.debug("Полученное LocationDto с автомобилями: {}", locationDto);

        verify(locationRepository, times(1)).findById(1L);
        verify(carRepository, times(1)).findByLocationId(1L);
        logger.info("Тест прошёл: getLocationByCarId_ShouldReturnLocationDtoWithCars");
    }
}
