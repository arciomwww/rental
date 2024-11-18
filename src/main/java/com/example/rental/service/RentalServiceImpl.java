package com.example.rental.service;

import com.example.rental.dto.RentalDto;
import com.example.rental.entity.*;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.RentalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public RentalDto createRental(RentalDto rentalDto) {
        Rental rental = new Rental();

        setCarProperties(rental, rentalDto);

        BigDecimal totalPrice = calculateTotalPrice(rentalDto);
        rental.setTotalPrice(totalPrice);

        Rental savedRental = rentalRepository.save(rental);
        log.info("Created rental: {}", savedRental);
        return mapToDto(savedRental);
    }


    @Transactional
    public RentalDto updateRental(Long id, RentalDto rentalDto) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Rental not found with id {}", id);
                    return new ResourceNotFoundException("Rental not found with id " + id);
                });

        setCarProperties(rental, rentalDto);

        Rental updatedRental = rentalRepository.save(rental);
        log.info("Updated rental: Успешно");
        return mapToDto(updatedRental);
    }

    public void deleteRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
        rentalRepository.delete(rental);
        log.info("Deleted rental: Успешно");
    }

    public Page<RentalDto> getAllRentals(Pageable pageable) {
        Page<Rental> rentalPage = rentalRepository.findAll(pageable);
        log.info("Retrieved all rentals");
        return rentalPage.map(this::mapToDto);
    }

    public RentalDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
        log.info("Retrieved rental: Успешно");
        return mapToDto(rental);
    }
    //История аренды для клиента
    public List<RentalDto> getUserRentalHistory(Long userId) {
        List<Rental> rentals = rentalRepository.findByUserId(userId);

        if (rentals.isEmpty()) {
            throw new ResourceNotFoundException("No rental history found for user with id: " + userId);
        }

        return rentals.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    //Просмотр истории аренды конкретного авто администратором
    public List<RentalDto> getRentalsByCarId(Long carId, Pageable pageable) {
        Page<Rental> rentalPage = rentalRepository.findByCarId(carId, pageable);
        log.info("Retrieved rentals for car with ID: {}", carId);
        return rentalPage.getContent().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public BigDecimal calculateTotalPrice(RentalDto rentalDto) {
        BigDecimal totalPrice;

        long hours = java.time.Duration.between(rentalDto.getStartDate(), rentalDto.getEndDate()).toHours();

        if ("HOURLY".equalsIgnoreCase(rentalDto.getTariffType())) {
            // Почасовой тариф
            totalPrice = rentalDto.getHourlyRate().multiply(BigDecimal.valueOf(hours));
        } else if ("SUBSCRIPTION".equalsIgnoreCase(rentalDto.getTariffType())) {
            // Абонементный тариф
            totalPrice = rentalDto.getSubscriptionRate();
        } else {
            throw new IllegalArgumentException("Unknown tariff type: " + rentalDto.getTariffType());
        }

        // Применение скидки, если указана
        if (rentalDto.getDiscount() != null && rentalDto.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = totalPrice.multiply(rentalDto.getDiscount())
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            totalPrice = totalPrice.subtract(discountAmount);
        }

        return totalPrice;
    }

    private void setCarProperties(Rental rental, RentalDto rentalDto) {
        rental.setCar(new Car(rentalDto.getCarId()));
        rental.setUser(new User(rentalDto.getUserId()));
        rental.setStartDate(rentalDto.getStartDate());
        rental.setEndDate(rentalDto.getEndDate());
        rental.setTotalPrice(rentalDto.getTotalPrice());
        rental.setMileage(rentalDto.getMileage());
        rental.setAdditionalInfo(rentalDto.getAdditionalInfo());
    }

    private RentalDto mapToDto(Rental rental) {
        return RentalDto.builder()
                .id(rental.getId())
                .carId(rental.getCar().getId())
                .userId(rental.getUser().getId())
                .startDate(rental.getStartDate())
                .endDate(rental.getEndDate())
                .totalPrice(rental.getTotalPrice())
                .mileage(rental.getMileage())
                .additionalInfo(rental.getAdditionalInfo())
                .build();
    }
}
