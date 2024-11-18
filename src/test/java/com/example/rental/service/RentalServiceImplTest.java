package com.example.rental.service;

import com.example.rental.dto.RentalDto;
import com.example.rental.entity.Car;
import com.example.rental.entity.User;
import com.example.rental.entity.Rental;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImplTest.class);

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private RentalDto rentalDto;

    @BeforeEach
    void setUp() {
        rentalDto = RentalDto.builder()
                .carId(2L)
                .userId(3L)
                .startDate(LocalDateTime.of(2024, 11, 1, 10, 0))
                .endDate(LocalDateTime.of(2024, 11, 2, 10, 0))
                .tariffType("HOURLY")
                .hourlyRate(new BigDecimal("15.50"))
                .discount(new BigDecimal("10.0"))
                .mileage(100)
                .additionalInfo("Оплата за камри")
                .build();
    }

    @Test
    void testCreateRental() {
        Car car = new Car();
        car.setId(2L);

        User user = new User();
        user.setId(3L);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCar(car);
        rental.setUser(user);

        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        RentalDto createdRental = rentalService.createRental(rentalDto);

        assertNotNull(createdRental);
        assertEquals(1L, createdRental.getId());
        logger.info("Тест пройден: аренда создана успешно с ID: {}", createdRental.getId());
    }

    @Test
    void testUpdateRental() {
        Rental existingRental = new Rental();
        existingRental.setId(2L);
        when(rentalRepository.findById(2L)).thenReturn(Optional.of(existingRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(existingRental);

        RentalDto updatedRental = rentalService.updateRental(2L, rentalDto);

        assertNotNull(updatedRental);
        assertEquals(2L, updatedRental.getId());
        logger.info("Тест пройден: аренда с ID {} обновлена успешно", updatedRental.getId());
    }

    @Test
    void testDeleteRental() {
        Rental existingRental = new Rental();
        existingRental.setId(3L);
        when(rentalRepository.findById(3L)).thenReturn(Optional.of(existingRental));

        rentalService.deleteRental(3L);

        verify(rentalRepository, times(1)).delete(existingRental);
        logger.info("Тест пройден: аренда с ID 3 удалена успешно");
    }

    @Test
    void testGetRentalById() {
        Car car = new Car();
        car.setId(1L);

        User user = new User();
        user.setId(3L);

        Rental existingRental = new Rental();
        existingRental.setId(1L);
        existingRental.setCar(car);
        existingRental.setUser(user);

        when(rentalRepository.findById(1L)).thenReturn(Optional.of(existingRental));

        RentalDto rental = rentalService.getRentalById(1L);

        assertNotNull(rental);
        assertEquals(1L, rental.getId());
        logger.info("Тест пройден: аренда с ID {} успешно получена", rental.getId());
    }

    @Test
    void testGetAllRentals() {
        // Инициализация списка аренды
        List<Rental> rentals = new ArrayList<>();

        // Создание первого аренды
        Rental rental1 = new Rental();
        rental1.setId(1L);
        Car car1 = new Car();
        car1.setId(1L);
        rental1.setCar(car1); // Установите Car для аренды
        User user1 = new User();
        user1.setId(3L);
        rental1.setUser(user1); // Установите User для аренды
        rentals.add(rental1);

        // Создание второго аренды
        Rental rental2 = new Rental();
        rental2.setId(2L);
        Car car2 = new Car();
        car2.setId(2L);
        rental2.setCar(car2); // Установите Car для аренды
        User user2 = new User();
        user2.setId(4L);
        rental2.setUser(user2); // Установите User для аренды
        rentals.add(rental2);

        // Создание страницы аренды
        Page<Rental> rentalPage = new PageImpl<>(rentals);
        when(rentalRepository.findAll(any(Pageable.class))).thenReturn(rentalPage);

        // Вызов метода
        Page<RentalDto> rentalDtos = rentalService.getAllRentals(Pageable.unpaged());

        // Проверка результатов
        assertEquals(2, rentalDtos.getContent().size());
        logger.info("Тест пройден: все аренды успешно получены");
    }


    @Test
    void testGetUserRentalHistory() {
        // Создаем пользователя
        User user = new User();
        user.setId(3L); // Установите ID для User

        // Создаем аренду и связываем с пользователем
        Car car = new Car();
        car.setId(1L); // Установите ID для Car

        List<Rental> rentals = new ArrayList<>();
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCar(car); // Установите Car для аренды
        rental.setUser(user); // Устанавливаем User для аренды
        rentals.add(rental);

        when(rentalRepository.findByUserId(3L)).thenReturn(rentals);

        List<RentalDto> rentalHistory = rentalService.getUserRentalHistory(3L);

        assertEquals(1, rentalHistory.size());
        assertEquals(1L, rentalHistory.get(0).getId());
        logger.info("Тест пройден: история аренды пользователя успешно получена");
    }


    @Test
    void testUpdateRentalNotFound() {
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> rentalService.updateRental(999L, rentalDto));

        assertTrue(exception.getMessage().contains("Rental not found with id"));
        logger.info("Тест пройден: попытка обновления несуществующей аренды вызвала исключение");
    }
}
