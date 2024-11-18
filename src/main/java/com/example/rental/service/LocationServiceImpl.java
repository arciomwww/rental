package com.example.rental.service;

import com.example.rental.dto.CarDto;
import com.example.rental.dto.LocationDto;
import com.example.rental.entity.Car;
import com.example.rental.entity.Location;
import com.example.rental.exception.ResourceNotFoundException;
import com.example.rental.repository.CarRepository;
import com.example.rental.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {

    private final CarRepository carRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(CarRepository carRepository, LocationRepository locationRepository) {
        this.carRepository = carRepository;
        this.locationRepository = locationRepository;
    }

    public LocationDto createLocation(LocationDto locationDto) {
        if (locationDto.getParkingLot() == null) {
            throw new IllegalArgumentException("Parking lot cannot be null");
        }

        Location location = new Location();
        location.setParkingLot(locationDto.getParkingLot());
        location.setAddress(locationDto.getAddress());
        location.setCity(locationDto.getCity());
        location.setCountry(locationDto.getCountry());

        Location savedLocation = locationRepository.save(location);
        log.info("Created location: {}", savedLocation);
        return mapToDto(savedLocation);
    }

    public Page<LocationDto> getAllLocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Location> locations = locationRepository.findAll(pageable);
        log.info("Retrieved all locations (page: {}, size: {})", page, size);
        return locations.map(this::mapToDto);
    }

    public LocationDto getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Location not found with id {}", id);
                    return new ResourceNotFoundException("Location not found with id " + id);
                });
        log.info("Retrieved location: {}", location);
        return mapToDto(location);
    }

    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Location not found with id {}", id);
                    return new ResourceNotFoundException("Location not found with id " + id);
                });

        location.setParkingLot(locationDto.getParkingLot());
        location.setAddress(locationDto.getAddress());
        location.setCity(locationDto.getCity());
        location.setCountry(locationDto.getCountry());

        Location updatedLocation = locationRepository.save(location);
        log.info("Updated location: {}", updatedLocation);
        return mapToDto(updatedLocation);
    }

    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Location not found with id {}", id);
                    return new ResourceNotFoundException("Location not found with id " + id);
                });
        locationRepository.delete(location);
        log.info("Deleted location with id {}", id);
    }

    public List<LocationDto> getLocationsByCity(String city) {
        List<Location> locations = locationRepository.findByCity(city);
        log.info("Retrieved locations in city: {}", city);
        return locations.stream().map(this::mapToDto).toList();
    }

    public List<LocationDto> getLocationsByCountry(String country) {
        List<Location> locations = locationRepository.findByCountry(country);
        log.info("Retrieved locations in country: {}", country);
        return locations.stream().map(this::mapToDto).toList();
    }
    //Просмотр детальной информации о точке проката.
    public LocationDto getLocationByCarId(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id " + id));

        List<Car> carsAtLocation = carRepository.findByLocationId(id);
        List<CarDto> carDtos = carsAtLocation.stream()
                .map(this::mapCarToDto)
                .toList();

        LocationDto locationDto = mapToDto(location);
        locationDto.setCars(carDtos);

        return locationDto;
    }

    private CarDto mapCarToDto(Car car) {
        return CarDto.builder()
                .id(car.getId())
                .modelId(car.getModel().getId())
                .locationId(car.getLocation().getId())
                .status(car.getStatus())
                .pricePerHour(car.getPricePerHour())
                .pricePerDay(car.getPricePerDay())
                .build();
    }

    private LocationDto mapToDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .parkingLot(location.getParkingLot())
                .address(location.getAddress())
                .city(location.getCity())
                .country(location.getCountry())
                .build();
    }
}