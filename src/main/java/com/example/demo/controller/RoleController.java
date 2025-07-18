package com.example.demo.controller;

import com.example.demo.dto.requests.PermissionRequest;
import com.example.demo.dto.requests.RolePermissionRequest;
import com.example.demo.dto.requests.RoleRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/add-permissions")
    public ResponseEntity<?> addPermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.setPermissions(rolePermissionRequest);
        return ResponseEntity.ok(permissionSet);
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/update-permissions")
    public ResponseEntity<?> updatePermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.updatePermissions(rolePermissionRequest);
        return ResponseEntity.ok(permissionSet);
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/delete-permissions")
    public ResponseEntity<?> deletePermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.deletePermissions(rolePermissionRequest);
        return ResponseEntity.ok(permissionSet);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/get-permissions")
    public ResponseEntity<?> getPermissions(@RequestBody RolePermissionRequest rolePermissionRequest) {
        Set<Permission> permissionSet = roleService.getPermissions(rolePermissionRequest.getRoleName());
        return ResponseEntity.ok(permissionSet);
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/add-role")
    public ResponseEntity<?> addRole(@RequestBody RoleRequest roleRequest) {
        List<Role> roles = roleService.addRole(roleRequest.getRole());
        return ResponseEntity.ok(roles);
    }


    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body("Role deleted successfully");
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/get-roles")
    public ResponseEntity<?> getRoles() {
        List<Role> roles = roleService.getRoles();
        return ResponseEntity.ok(roles);
    }
}
