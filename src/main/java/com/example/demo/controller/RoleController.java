package com.example.demo.controller;

import com.example.demo.dto.RolePermissionDTO;
import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionRepository permissionRepository;

    @PostMapping("/add-permissions")
    public ResponseEntity<?> addPermissions(@RequestBody RolePermissionDTO rolePermissionDTO) {
        Set<Permission> permissionSet = roleService.setPermissions(rolePermissionDTO);
        return ResponseEntity.ok(permissionSet);
    }

    @PutMapping("/update-permissions")
    public ResponseEntity<?> updatePermissions(@RequestBody RolePermissionDTO rolePermissionDTO) {
        Set<Permission> permissionSet = roleService.updatePermissions(rolePermissionDTO);
        return ResponseEntity.ok(permissionSet);
    }

    @DeleteMapping("/delete-permissions")
    public ResponseEntity<?> deletePermissions(@RequestBody RolePermissionDTO rolePermissionDTO) {
        Set<Permission> permissionSet = roleService.deletePermissions(rolePermissionDTO);
        return ResponseEntity.ok(permissionSet);
    }

    @GetMapping("/get-permissions")
    public ResponseEntity<?> getPermissions(@RequestBody RolePermissionDTO rolePermissionDTO) {
        Set<Permission> permissionSet = roleService.getPermissions(rolePermissionDTO.getRoleName());
        return ResponseEntity.ok(permissionSet);
    }
}
