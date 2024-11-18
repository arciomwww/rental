package com.example.rental.service;

import com.example.rental.dto.UserRolesDto;
import com.example.rental.entity.Role;
import com.example.rental.entity.User;
import com.example.rental.entity.UserRoles;
import com.example.rental.repository.RoleRepository;
import com.example.rental.repository.UserRepository;
import com.example.rental.repository.UserRolesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRolesServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(UserRolesServiceImplTest.class);
    private AutoCloseable mocks;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRolesServiceImpl userRolesService;

    private UserRoles userRoles;
    private User user;
    private Role role;
    private UserRolesDto userRolesDto;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        user = User.builder().id(1L).build();
        role = Role.builder().id(1L).name("ROLE_USER").build();
        userRoles = UserRoles.builder().id(1L).user(user).role(role).build();
        userRolesDto = UserRolesDto.builder().id(1L).userId(1L).roleId(1L).build();

        logger.info("Тестовые данные инициализированы: {}", userRoles);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
            logger.info("Мок-объекты закрыты.");
        }
    }

    @Test
    void getAllUserRoles_Success() {
        List<UserRoles> userRolesList = new ArrayList<>();
        userRolesList.add(userRoles);
        Page<UserRoles> userRolesPage = new PageImpl<>(userRolesList);

        when(userRolesRepository.findAll(any(Pageable.class))).thenReturn(userRolesPage);
        logger.info("Смоделирован возврат всех ролей пользователей.");

        Page<UserRolesDto> userRolesDtoPage = userRolesService.getAllUserRoles(Pageable.unpaged());

        assertNotNull(userRolesDtoPage);
        assertEquals(1, userRolesDtoPage.getTotalElements());
        assertEquals(userRolesDto.getUserId(), userRolesDtoPage.getContent().get(0).getUserId());
        logger.info("Успешно получены все роли пользователей. Количество: {}", userRolesDtoPage.getTotalElements());
    }

    @Test
    void getUserRoleById_Success() {
        when(userRolesRepository.findById(userRoles.getId())).thenReturn(Optional.of(userRoles));
        logger.info("Смоделирован поиск роли пользователя по ID: {}", userRoles.getId());

        UserRolesDto foundUserRole = userRolesService.getUserRoleById(userRoles.getId());

        assertNotNull(foundUserRole);
        assertEquals(userRoles.getUser().getId(), foundUserRole.getUserId());
        assertEquals(userRoles.getRole().getId(), foundUserRole.getRoleId());
        logger.info("Успешно найдена роль пользователя. ID: {}, Роль: {}", foundUserRole.getUserId(), foundUserRole.getRoleId());
    }

    @Test
    void getUserRoleById_NotFound() {
        when(userRolesRepository.findById(userRoles.getId())).thenReturn(Optional.empty());
        logger.info("Смоделирован поиск роли пользователя по ID: {} (не найдено)", userRoles.getId());

        UserRolesDto foundUserRole = userRolesService.getUserRoleById(userRoles.getId());

        assertNull(foundUserRole);
        logger.info("Роль пользователя не найдена. ID: {}", userRoles.getId());
    }

    @Test
    void updateUserRole_Success() {
        when(userRolesRepository.findById(userRoles.getId())).thenReturn(Optional.of(userRoles));
        when(userRepository.findById(userRolesDto.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findById(userRolesDto.getRoleId())).thenReturn(Optional.of(role));
        when(userRolesRepository.save(any(UserRoles.class))).thenReturn(userRoles);
        logger.info("Смоделировано обновление роли пользователя с ID: {}", userRoles.getId());

        UserRolesDto updatedUserRole = userRolesService.updateUserRole(userRoles.getId(), userRolesDto);

        assertNotNull(updatedUserRole);
        assertEquals(userRolesDto.getUserId(), updatedUserRole.getUserId());
        assertEquals(userRolesDto.getRoleId(), updatedUserRole.getRoleId());

        ArgumentCaptor<UserRoles> userRolesCaptor = ArgumentCaptor.forClass(UserRoles.class);
        verify(userRolesRepository, times(1)).save(userRolesCaptor.capture());
        assertEquals(userRolesDto.getUserId(), userRolesCaptor.getValue().getUser().getId());
        assertEquals(userRolesDto.getRoleId(), userRolesCaptor.getValue().getRole().getId());

        logger.info("Успешно обновлена роль пользователя. ID: {}, Новая Роль: {}", updatedUserRole.getUserId(), updatedUserRole.getRoleId());
    }

    @Test
    void updateUserRole_NotFound() {
        when(userRolesRepository.findById(userRoles.getId())).thenReturn(Optional.empty());
        logger.info("Попытка обновления роли пользователя с ID: {} (не найдено)", userRoles.getId());

        UserRolesDto updatedUserRole = userRolesService.updateUserRole(userRoles.getId(), userRolesDto);

        assertNull(updatedUserRole);
        verify(userRolesRepository, times(0)).save(any(UserRoles.class));
        logger.info("Роль пользователя не обновлена, так как роль не найдена. ID: {}", userRoles.getId());
    }

    @Test
    void updateUserRole_UserNotFound() {
        when(userRolesRepository.findById(userRoles.getId())).thenReturn(Optional.of(userRoles));
        when(userRepository.findById(userRolesDto.getUserId())).thenReturn(Optional.empty());
        logger.info("Смоделировано обновление роли пользователя с ID: {} (пользователь не найден)", userRoles.getId());

        UserRolesDto updatedUserRole = userRolesService.updateUserRole(userRoles.getId(), userRolesDto);

        assertNull(updatedUserRole);
        verify(userRolesRepository, times(0)).save(any(UserRoles.class));
        logger.info("Роль пользователя не обновлена, так как пользователь не найден. ID: {}", userRolesDto.getUserId());
    }

    @Test
    void updateUserRole_RoleNotFound() {
        when(userRolesRepository.findById(userRoles.getId())).thenReturn(Optional.of(userRoles));
        when(userRepository.findById(userRolesDto.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findById(userRolesDto.getRoleId())).thenReturn(Optional.empty());
        logger.info("Смоделировано обновление роли пользователя с ID: {} (роль не найдена)", userRoles.getId());

        UserRolesDto updatedUserRole = userRolesService.updateUserRole(userRoles.getId(), userRolesDto);

        assertNull(updatedUserRole);
        verify(userRolesRepository, times(0)).save(any(UserRoles.class));
        logger.info("Роль пользователя не обновлена, так как роль не найдена. ID: {}", userRolesDto.getRoleId());
    }

    @Test
    void deleteUserRole_Success() {
        when(userRolesRepository.existsById(userRoles.getId())).thenReturn(true);
        logger.info("Смоделировано удаление роли пользователя с ID: {}", userRoles.getId());

        boolean isDeleted = userRolesService.deleteUserRole(userRoles.getId());

        assertTrue(isDeleted);
        verify(userRolesRepository, times(1)).deleteById(userRoles.getId());
        logger.info("Успешно удалена роль пользователя с ID: {}", userRoles.getId());
    }

    @Test
    void deleteUserRole_NotFound() {
        when(userRolesRepository.existsById(userRoles.getId())).thenReturn(false);
        logger.info("Попытка удаления роли пользователя с ID: {} (не найдено)", userRoles.getId());

        boolean isDeleted = userRolesService.deleteUserRole(userRoles.getId());

        assertFalse(isDeleted);
        verify(userRolesRepository, times(0)).deleteById(userRoles.getId());
        logger.info("Роль пользователя не удалена, так как она не найдена. ID: {}", userRoles.getId());
    }
}
