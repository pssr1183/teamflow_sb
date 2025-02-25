package com.example.demo.controller;

import com.example.demo.dto.requests.RolePermissionRequest;
import com.example.demo.entity.Permission;
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

    @PostMapping("/add-permissions")
    public ResponseEntity<?> addPermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.setPermissions(rolePermissionRequest);
        return ResponseEntity.ok(permissionSet);
    }

    @PutMapping("/update-permissions")
    public ResponseEntity<?> updatePermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.updatePermissions(rolePermissionRequest);
        return ResponseEntity.ok(permissionSet);
    }

    @DeleteMapping("/delete-permissions")
    public ResponseEntity<?> deletePermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.deletePermissions(rolePermissionRequest);
        return ResponseEntity.ok(permissionSet);
    }

    @GetMapping("/get-permissions")
    public ResponseEntity<?> getPermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.getPermissions(rolePermissionRequest.getRoleName());
        return ResponseEntity.ok(permissionSet);
    }
}
