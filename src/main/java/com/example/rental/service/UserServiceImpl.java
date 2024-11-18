package com.example.rental.service;

import com.example.rental.dto.UserDto;
import com.example.rental.entity.Role;
import com.example.rental.entity.User;
import com.example.rental.entity.UserRoles;
import com.example.rental.repository.RoleRepository;
import com.example.rental.repository.UserRepository;
import com.example.rental.repository.UserRolesRepository;
import com.example.rental.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService, UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesRepository userRolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           UserRolesRepository userRolesRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRolesRepository = userRolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь %s не найден", username)));

        return UserDetailsImpl.build(user);
    }

    public User registerNewUser(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        Role role;
        if (userDto.getRole() != null && userDto.getRole().getName() != null) {
            role = roleRepository.findByName(userDto.getRole().getName())
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Role %s not found", userDto.getRole().getName())));
            log.info("Role found: {}", role.getName());
        } else {
            role = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("Role USER not found"));
            log.info("Default role found: {}", role.getName());
        }

        UserRoles userRole = UserRoles.builder()
                .user(user)
                .role(role)
                .build();

        userRolesRepository.save(userRole);
        return user;
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());

        userRepository.save(user);
        return userDto;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    public List<UserDto> findByUsername(String username) {
        Optional<User> users = userRepository.findByUsername(username);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDto> findByEmail(String email) {
        List<User> users = userRepository.findByEmail(email);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDto> findByFirstName(String firstName) {
        List<User> users = userRepository.findByFirstName(firstName);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDto> findByLastName(String lastName) {
        List<User> users = userRepository.findByLastName(lastName);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDto> findByEnabled(Boolean enabled) {
        List<User> users = userRepository.findByEnabled(enabled);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDto> findUsersByEnabledStatusNative(Boolean enabled) {
        List<User> users = userRepository.findUsersByEnabledStatusNative(enabled);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<UserDto> findUsersByFirstNameAndLastNameJPQL(String firstName, String lastName) {
        List<User> users = userRepository.findUsersByFirstNameAndLastNameJPQL(firstName, lastName);
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }


    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
