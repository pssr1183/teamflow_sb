package com.example.demo.service;

import com.example.demo.dto.requests.RolePermissionRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.exceptions.EntityAlreadyExistsException;
import com.example.demo.exceptions.PermissionNotFoundException;
import com.example.demo.exceptions.RoleNotFoundException;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public Set<Permission> setPermissions(RolePermissionRequest rolePermissionRequest) {

        Set<String> requestedPermissions = rolePermissionRequest.getPermissions();
        Set<Permission> validPermissions = this.validPermissions(requestedPermissions);

        String roleName = rolePermissionRequest.getRoleName();
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

    public Set<Permission> updatePermissions(RolePermissionRequest rolePermissionRequest) {

        Set<String> requestedPermissions = rolePermissionRequest.getPermissions();
        Set<Permission> validPermissions = this.validPermissions(requestedPermissions);

        String roleName = rolePermissionRequest.getRoleName();
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()-> new RoleNotFoundException("Role not found with name "+roleName)
        );

        Set<Permission> existingPermissions = role.getPermissions();
        existingPermissions.addAll(validPermissions);
        role.setPermissions(existingPermissions);
        roleRepository.save(role);

        return role.getPermissions();
    }

    public Set<Permission> deletePermissions(RolePermissionRequest rolePermissionRequest) {

        Set<String> requestedPermissions = rolePermissionRequest.getPermissions();
        Set<Permission> validPermissions = this.validPermissions(requestedPermissions);

        String roleName = rolePermissionRequest.getRoleName();
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

    public Set<Role> validRoles(Set<String> requestRoles) {
        Set<Role> defaultRoles = roleRepository.findByNameIn(requestRoles);
        // Identify missing roles
        Set<String> foundRoleNames = defaultRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> missingRoles = requestRoles.stream()
                .filter(role -> !foundRoleNames.contains(role))
                .collect(Collectors.toSet());

        // If any roles are missing, throw an error
        if (!missingRoles.isEmpty()) {
            throw new RoleNotFoundException("The following roles are not found in the database: " + missingRoles);
        }
        return defaultRoles;
    }

    public Set<Role> findByNameIn(Set<String> roleNames) {
        return roleRepository.findByNameIn(roleNames);
    }

    public List<Role> addRole(String newRole) {

        Role existingRole = roleRepository.findRoleByName(newRole).orElse(null);
        if(existingRole != null) {
            throw new  EntityAlreadyExistsException("The following role already exists");
        }
        Role role = new Role();
        role.setName(newRole);
        roleRepository.save(role);

        return roleRepository.findAll();
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }


    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(
                () -> new RoleNotFoundException("Role Doesn't exist")
        );

        roleRepository.deleteById(roleId);
    }
}
