package com.example.demo.service;

import com.example.demo.dto.requests.PermissionRequest;
import com.example.demo.dto.requests.RolePermissionRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.exceptions.EntityAlreadyExistsException;
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

public class PermissionService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> addPermissions(String newPermission) {

        Permission existingPermission = permissionRepository.findPermissionByName(newPermission).orElse(null);
        if(existingPermission != null) {
           throw new EntityAlreadyExistsException("The following Permission already exists");
        }
        Permission permission = new Permission();
        permission.setName(newPermission);
        permissionRepository.save(permission);

        return permissionRepository.findAll();
    }

    public List<Permission> getPermissions() {
        return permissionRepository.findAll();
    }


    public void deletePermissions(Long permissiomId) {
        Permission permission = permissionRepository.findById(permissiomId).orElseThrow(
                () -> new PermissionNotFoundException("Permission Doesn't exist")
        );

        permissionRepository.deleteById(permissiomId);
    }
}