package com.example.demo.controller;

import com.example.demo.dto.UserRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.security.JWTUtil;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private RoleRepository roleRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {

        if(userService.findByUsername(userRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("User is already present");
        }

        Set<String> roles = userRequest.getRolenames();
        Set<Role> defaultRoles = roleRepository.findByNameIn(roles);
        // Identify missing roles
        Set<String> foundRoleNames = defaultRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> missingRoles = roles.stream()
                .filter(role -> !foundRoleNames.contains(role))
                .collect(Collectors.toSet());

        // If any roles are missing, throw an error
        if (!missingRoles.isEmpty()) {
            return ResponseEntity.badRequest().body("The following roles are not found in the database: " + missingRoles);
        }

        // Register the user with the default role
        userService.register(userRequest.getUsername(),userRequest.getDisplayName(), userRequest.getPassword(), defaultRoles);
        return ResponseEntity.ok("User has been successfully Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User existingUser = userService.findByUsername(user.getUsername());
        if(existingUser == null) {
            return ResponseEntity.badRequest().body("User should register");
        }

        if(!passwordEncoder.matches(user.getPassword(),existingUser.getPassword())) {
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
