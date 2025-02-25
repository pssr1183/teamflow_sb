package com.example.demo.controller;

import com.example.demo.dto.requests.UserLoginRequest;
import com.example.demo.dto.requests.UserRegistrationRequest;
import com.example.demo.dto.requests.UserRolesUpdateRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.security.JWTUtil;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest userRequest) {

        if(userService.findByUsername(userRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("User is already present");
        }

        Set<String> roles = userRequest.getRolenames() != null && !userRequest.getRolenames().isEmpty() ? userRequest.getRolenames():
                Collections.singleton("User");

        Set<Role> defaultRoles = roleService.validRoles(roles);

        // Register the user with the default role
        userService.register(userRequest.getUsername(),userRequest.getDisplayName(), userRequest.getPassword(), defaultRoles);
        return ResponseEntity.ok("User has been successfully Registered");
    }

    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<?> updateRoles(@Valid @RequestBody UserRolesUpdateRequest dto, @PathVariable Long userId) {
        User user = userService.findUserById(userId);
        Set<String> roles = dto.getRolenames();

        Set<Role> vaildRoles = roleService.validRoles(roles);
        userService.updateRoles(user,vaildRoles);
        return ResponseEntity.ok("User roles are updated successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        User existingUser = userService.findByUsername(userLoginRequest.getUsername());

        if(existingUser == null) {
            return ResponseEntity.badRequest().body("User should register");
        }

        if(!passwordEncoder.matches(userLoginRequest.getPassword(),existingUser.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        // Create a map to hold claims (you can add more claims as necessary)
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", existingUser.getUsername());

        claims.put("roles",existingUser.getRoles().stream()
                .map(role -> "ROLE_"+role.getName())
                .collect(Collectors.toList()));
        claims.put("permissions", existingUser.getRoles()
                .stream().flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet()));

        String token = jwtUtil.generateToken(existingUser.getUsername(), claims); // You can add more claims if needed

        return ResponseEntity.ok("User has been successfully Logged In. Token: " + token);
    }

    @GetMapping("/get_allusers")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/fetch_roles")
    public ResponseEntity<?> getUserRoles(){

        Map<String, Set<String>> rolePermissionsMap = userService.getUserRoles();

        return ResponseEntity.ok(rolePermissionsMap);
    }

    @GetMapping("/fetch_current_user_roles")
    public ResponseEntity<?> getCurrentUserRoles(Principal principal ){
        User user = userService.findByUsername(principal.getName());

        Map<String, Set<String>> rolePermissionsMap = userService.getCurrentUserRoles(user);

        return ResponseEntity.ok(rolePermissionsMap);
    }

//    @GetMapping("/permissions")
//    public ResponseEntity<?> getUserPermissions(){
//        String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
//        User user = userService.findByUsername(username);
//        Set<Permission> permissionSet = userService.userPermissions(username);
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return ResponseEntity.ok(permissionSet);
//    }

}
