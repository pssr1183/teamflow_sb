package com.example.demo.controller;

import com.example.demo.dto.RolePermissionDTO;
import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionRepository permissionRepository;

    @PostMapping("/add-permissions")
    public ResponseEntity<?> addPermissions(@RequestBody RolePermissionDTO rolePermissionDTO) {
        Set<String> requestedPermissions = rolePermissionDTO.getPermissions();
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<String> existingPermissionNames = allPermissions.stream().map(Permission::getName).collect(Collectors.toSet());
        Set<String> missingPermissions = requestedPermissions.stream().filter(permission -> !existingPermissionNames.contains(permission)).collect(Collectors.toSet());

        if(!missingPermissions.isEmpty()) {
            return ResponseEntity.badRequest().body("The following roles are not found in the database: "+missingPermissions);
        }
        Set<Permission> validPermissions = permissionRepository.findByNameIn(requestedPermissions);

        Set<Permission> permissionSet = roleService.setPermissions(rolePermissionDTO.getRoleName(),validPermissions);

        return ResponseEntity.ok(permissionSet);
    }
}
