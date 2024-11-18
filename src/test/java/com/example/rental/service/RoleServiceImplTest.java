package com.example.rental.service;

import com.example.rental.dto.RoleDto;
import com.example.rental.entity.Role;
import com.example.rental.repository.RoleRepository;
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

class RoleServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImplTest.class);
    private AutoCloseable mocks;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        role = Role.builder().id(1L).name("ROLE_USER").build();
        roleDto = RoleDto.builder().id(1L).name("ROLE_USER").build();
        logger.info("Настройка тестов: роль = {}, roleDto = {}", role, roleDto);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
        logger.info("Завершение тестов");
    }

    @Test
    void createRole_Success() {
        logger.info("Запуск теста createRole_Success");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto createdRole = roleService.createRole(roleDto);

        assertNotNull(createdRole);
        assertEquals(role.getId(), createdRole.getId());
        assertEquals(role.getName(), createdRole.getName());

        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, times(1)).save(roleCaptor.capture());
        assertEquals(roleDto.getName(), roleCaptor.getValue().getName());
        logger.info("Роль успешно создана: {}", createdRole);
    }

    @Test
    void getAllRoles_Success() {
        logger.info("Запуск теста getAllRoles_Success");
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        Page<Role> rolePage = new PageImpl<>(roles);

        when(roleRepository.findAll(any(Pageable.class))).thenReturn(rolePage);

        Page<RoleDto> roleDtoPage = roleService.getAllRoles(Pageable.unpaged());

        assertNotNull(roleDtoPage);
        assertEquals(1, roleDtoPage.getTotalElements());
        assertEquals(role.getId(), roleDtoPage.getContent().get(0).getId());
        assertEquals(role.getName(), roleDtoPage.getContent().get(0).getName());
        logger.info("Получены роли: {}", roleDtoPage.getContent());
    }

    @Test
    void getRoleById_Success() {
        logger.info("Запуск теста getRoleById_Success");
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        RoleDto foundRole = roleService.getRoleById(role.getId());

        assertNotNull(foundRole);
        assertEquals(role.getId(), foundRole.getId());
        assertEquals(role.getName(), foundRole.getName());
        logger.info("Роль найдена: {}", foundRole);
    }

    @Test
    void getRoleById_NotFound() {
        logger.info("Запуск теста getRoleById_NotFound");
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        RoleDto foundRole = roleService.getRoleById(role.getId());

        assertNull(foundRole);
        logger.info("Роль не найдена для ID: {}", role.getId());
    }

    @Test
    void updateRole_Success() {
        logger.info("Запуск теста updateRole_Success");
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto updatedRoleDto = RoleDto.builder().id(1L).name("ROLE_ADMIN").build();
        RoleDto updatedRole = roleService.updateRole(role.getId(), updatedRoleDto);

        assertNotNull(updatedRole);
        assertEquals(updatedRoleDto.getId(), updatedRole.getId());
        assertEquals(updatedRoleDto.getName(), updatedRole.getName());

        verify(roleRepository, times(1)).save(any(Role.class));
        logger.info("Роль успешно обновлена: {}", updatedRole);
    }

    @Test
    void updateRole_NotFound() {
        logger.info("Запуск теста updateRole_NotFound");
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());

        RoleDto updatedRole = roleService.updateRole(role.getId(), roleDto);

        assertNull(updatedRole);
        verify(roleRepository, times(0)).save(any(Role.class));
        logger.info("Не удалось обновить роль, так как она не найдена для ID: {}", role.getId());
    }

    @Test
    void deleteRole_Success() {
        logger.info("Запуск теста deleteRole_Success");
        when(roleRepository.existsById(role.getId())).thenReturn(true);

        boolean isDeleted = roleService.deleteRole(role.getId());

        assertTrue(isDeleted);
        verify(roleRepository, times(1)).deleteById(role.getId());
        logger.info("Роль успешно удалена для ID: {}", role.getId());
    }

    @Test
    void deleteRole_NotFound() {
        logger.info("Запуск теста deleteRole_NotFound");
        when(roleRepository.existsById(role.getId())).thenReturn(false);

        boolean isDeleted = roleService.deleteRole(role.getId());

        assertFalse(isDeleted);
        verify(roleRepository, times(0)).deleteById(role.getId());
        logger.info("Не удалось удалить роль, так как она не найдена для ID: {}", role.getId());
    }
}
