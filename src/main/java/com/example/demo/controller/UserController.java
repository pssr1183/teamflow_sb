package com.example.demo.controller;

import com.example.demo.dto.UserRequest;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        if(userService.findByUsername(userRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("User is already present");
        }
        Role defaultRole = roleRepository.findRoleByName(userRequest.getRolename());
        if (defaultRole == null) {
            return ResponseEntity.badRequest().body("Default role not found in the database");
        }

        // Register the user with the default role
        userService.register(userRequest.getUsername(), userRequest.getPassword(), defaultRole);
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
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        // Authenticate the user using the AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // If authentication is successful, set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().toString();
        return ResponseEntity.ok("User has been successfully Logged In "+username+" with role "+role);
    }

    @PreAuthorize("hasRole('Member')")
    @GetMapping("/permissions")
    public ResponseEntity<?> getUserPermissions(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
        User user = userService.findByUsername(username);
        Set<Permission> permissionSet = userService.userPermissions(username);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        user.getRole().getPermissions().forEach(p -> System.out.println(p.getName()));
//        auth.getAuthorities().forEach(authority -> System.out.println("Authority: " + authority.getAuthority()));
        return ResponseEntity.ok(permissionSet);
    }
}
