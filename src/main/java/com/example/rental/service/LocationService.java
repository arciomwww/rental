package com.example.rental.service;

import com.example.rental.dto.LocationDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LocationService {
    LocationDto createLocation(LocationDto locationDto);
    LocationDto getLocationById(Long id);
    LocationDto updateLocation(Long id, LocationDto locationDto);
    LocationDto getLocationByCarId(Long id);
    void deleteLocation(Long id);

    List<LocationDto> getLocationsByCity(String city);
    List<LocationDto> getLocationsByCountry(String country);

    Page<LocationDto> getAllLocations(int page, int size);
}
