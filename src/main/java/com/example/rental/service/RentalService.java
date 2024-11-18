package com.example.rental.service;

import com.example.rental.dto.RentalDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RentalService {
    RentalDto createRental(RentalDto rentalDto);
    RentalDto updateRental(Long id, RentalDto rentalDto);
    RentalDto getRentalById(Long id);
    void deleteRental(Long id);

    List<RentalDto> getUserRentalHistory(Long userId);
    List<RentalDto> getRentalsByCarId(Long carId, Pageable pageable);

    BigDecimal calculateTotalPrice(RentalDto rentalDto);

    Page<RentalDto> getAllRentals(Pageable pageable);
}
