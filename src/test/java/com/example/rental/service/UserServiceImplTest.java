package com.example.rental.service;

import com.example.rental.dto.UserDto;
import com.example.rental.entity.Role;
import com.example.rental.entity.User;
import com.example.rental.entity.UserRoles;
import com.example.rental.repository.RoleRepository;
import com.example.rental.repository.UserRepository;
import com.example.rental.repository.UserRolesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private AutoCloseable mocks;
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }
    @Test
    public void testRegisterNewUser() {
        // Данные для нового пользователя
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("testPassword");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");

        // Мокирование роли
        Role role = new Role();
        role.setName("ROLE_USER");

        // Мокирование кодирования пароля
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // Мокирование сохранения пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdateAt(LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Вызов метода регистрации
        User registeredUser = userService.registerNewUser(userDto);

        // Проверка результатов
        assertNotNull(registeredUser);
        assertEquals("testUser", registeredUser.getUsername());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());

        // Проверка вызовов
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRolesRepository, times(1)).save(any(UserRoles.class));
    }

    @Test
    public void testFindById() {
        // Данные пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Вызов метода поиска
        UserDto foundUser = userService.findById(1L);

        // Проверка результатов
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());

        // Проверка вызовов
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateUser() {
        // Данные существующего пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUser");
        user.setEmail("old@example.com");

        // Данные для обновления
        UserDto updateUserDto = new UserDto();
        updateUserDto.setUsername("updatedUser");
        updateUserDto.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Вызов метода обновления
        UserDto updatedUser = userService.updateUser(1L, updateUserDto);

        // Проверка результатов
        assertEquals("updatedUser", user.getUsername());
        assertEquals("updated@example.com", user.getEmail());

        // Проверка вызовов
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        // Вызов метода удаления
        userService.deleteUser(userId);

        // Проверка вызовов
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testGetAllUsers() {
        // Данные для теста
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Вызов метода получения всех пользователей
        Page<UserDto> userDtos = userService.getAllUsers(Pageable.unpaged());

        // Проверка результатов
        assertEquals(1, userDtos.getContent().size());
        assertEquals("testUser", userDtos.getContent().get(0).getUsername());

        // Проверка вызовов
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testFindByUsername() {
        // Данные пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Вызов метода поиска по имени пользователя
        List<UserDto> users = userService.findByUsername("testUser");

        // Проверка результатов
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());

        // Проверка вызовов
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    public void testFindByEmail() {
        // Данные пользователя
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Collections.singletonList(user));

        // Вызов метода поиска по email
        List<UserDto> users = userService.findByEmail("test@example.com");

        // Проверка результатов
        assertEquals(1, users.size());
        assertEquals("test@example.com", users.get(0).getEmail());

        // Проверка вызовов
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testFindByFirstName() {
        // Данные пользователя
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");

        when(userRepository.findByFirstName("Test")).thenReturn(Collections.singletonList(user));

        // Вызов метода поиска по имени
        List<UserDto> users = userService.findByFirstName("Test");

        // Проверка результатов
        assertEquals(1, users.size());
        assertEquals("Test", users.get(0).getFirstName());

        // Проверка вызовов
        verify(userRepository, times(1)).findByFirstName("Test");
    }

    @Test
    public void testFindByLastName() {
        // Данные пользователя
        User user = new User();
        user.setId(1L);
        user.setLastName("User");

        when(userRepository.findByLastName("User")).thenReturn(Collections.singletonList(user));

        // Вызов метода поиска по фамилии
        List<UserDto> users = userService.findByLastName("User");

        // Проверка результатов
        assertEquals(1, users.size());
        assertEquals("User", users.get(0).getLastName());

        // Проверка вызовов
        verify(userRepository, times(1)).findByLastName("User");
    }
}
