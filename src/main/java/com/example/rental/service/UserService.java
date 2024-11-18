package com.example.rental.service;

import com.example.rental.dto.UserDto;
import com.example.rental.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User registerNewUser(UserDto userDto);

    UserDto findById(Long id);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    List<UserDto> findByUsername(String username);
    List<UserDto> findByEmail(String email);
    List<UserDto> findByFirstName(String firstName);
    List<UserDto> findByLastName(String lastName);
    List<UserDto> findByEnabled(Boolean enabled);
    List<UserDto> findUsersByEnabledStatusNative(Boolean enabled);
    List<UserDto> findUsersByFirstNameAndLastNameJPQL(String firstName, String lastName);

}
