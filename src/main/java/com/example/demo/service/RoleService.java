package com.example.demo.service;

import com.example.demo.dto.RolePermissionDTO;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.exceptions.PermissionNotFoundException;
import com.example.demo.exceptions.RoleNotFoundException;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public Set<Permission> setPermissions(RolePermissionDTO rolePermissionDTO) {

        Set<String> requestedPermissions = rolePermissionDTO.getPermissions();
        Set<Permission> validPermissions = this.validPermissions(requestedPermissions);

        String roleName = rolePermissionDTO.getRoleName();
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()-> new RoleNotFoundException("Role not found with name "+roleName)
        );

        role.setPermissions(validPermissions);
        roleRepository.save(role);

        return role.getPermissions();
    }

    public Set<Permission> getPermissions(String roleName) {

        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()-> new RoleNotFoundException("Role not found with name "+roleName)
        );

        return role.getPermissions();
    }

    public Set<Permission> updatePermissions(RolePermissionDTO rolePermissionDTO) {

        Set<String> requestedPermissions = rolePermissionDTO.getPermissions();
        Set<Permission> validPermissions = this.validPermissions(requestedPermissions);

        String roleName = rolePermissionDTO.getRoleName();
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()-> new RoleNotFoundException("Role not found with name "+roleName)
        );

        Set<Permission> existingPermissions = role.getPermissions();
        existingPermissions.addAll(validPermissions);
        role.setPermissions(existingPermissions);
        roleRepository.save(role);

        return role.getPermissions();
    }

    public Set<Permission> deletePermissions(RolePermissionDTO rolePermissionDTO) {

        Set<String> requestedPermissions = rolePermissionDTO.getPermissions();
        Set<Permission> validPermissions = this.validPermissions(requestedPermissions);

        String roleName = rolePermissionDTO.getRoleName();
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()-> new RoleNotFoundException("Role not found with name "+roleName)
        );

        Set<String> permissionNamesToRemove = validPermissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        Set<Permission> existingPermissions = role.getPermissions();
        existingPermissions.removeIf(permission -> permissionNamesToRemove.contains(permission.getName()));
        role.setPermissions(existingPermissions);
        roleRepository.save(role);

        return role.getPermissions();
    }

    public Set<Permission> validPermissions(Set<String> requestedPermissions) {

        List<Permission> allPermissions = permissionRepository.findAll();
        Set<String> existingPermissionNames = allPermissions.stream().map(Permission::getName).collect(Collectors.toSet());
        Set<String> missingPermissions = requestedPermissions.stream().filter(permission -> !existingPermissionNames.contains(permission)).collect(Collectors.toSet());

        if(!missingPermissions.isEmpty()) {
            throw new PermissionNotFoundException("The following roles are not found in the database: "+missingPermissions);
        }

        Set<Permission> validPermissions = permissionRepository.findByNameIn(requestedPermissions);

        return validPermissions;
    }
}
