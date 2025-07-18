package com.example.demo.controller;

import com.example.demo.dto.requests.PermissionRequest;
import com.example.demo.dto.requests.RolePermissionRequest;
import com.example.demo.entity.Permission;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("hasRole('Admin')")
    @PostMapping("/add-permission")
    public ResponseEntity<?> addPermission(@RequestBody PermissionRequest permissionRequest) {
        List<Permission> permissionSet = permissionService.addPermissions(permissionRequest.getPermission());
        return ResponseEntity.ok(permissionSet);
    }


    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/delete-permission/{id}")
    public ResponseEntity<?> deletePermissions(@PathVariable Long id) {
        permissionService.deletePermissions(id);
        return ResponseEntity.status(HttpStatus.OK).body("Permission deleted successfully");
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/get-permissions")
    public ResponseEntity<?> getPermissions() {
        List<Permission> permissionSet = permissionService.getPermissions();
        return ResponseEntity.ok(permissionSet);
    }
}
