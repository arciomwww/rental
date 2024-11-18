package com.example.rental.service;

import com.example.rental.dto.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleDto createRole(RoleDto roleDto);
    RoleDto getRoleById(Long id);
    RoleDto updateRole(Long id, RoleDto roleDto);
    boolean deleteRole(Long id);

    Page<RoleDto> getAllRoles(Pageable pageable);
}
