package com.example.rental.service;

import com.example.rental.dto.UserRolesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRolesService {
    UserRolesDto getUserRoleById(Long id);
    UserRolesDto updateUserRole(Long id, UserRolesDto userRolesDto);
    boolean deleteUserRole(Long id);

    Page<UserRolesDto> getAllUserRoles(Pageable pageable);
}
