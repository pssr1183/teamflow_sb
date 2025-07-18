package com.example.demo.controller;

import com.example.demo.dto.UserDisplayDTO;
import com.example.demo.dto.requests.*;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.security.JWTUtil;
import com.example.demo.service.RateLimiterService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private RateLimiterService rateLimiterService;

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
        Boolean isRequestAllowed = rateLimiterService.isAllowed("login"+userLoginRequest.getUsername());
        if(!isRequestAllowed) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests. Try again later.");
        }
        User existingUser = userService.findByUsername(userLoginRequest.getUsername());

        if(existingUser == null) {
            return ResponseEntity.badRequest().body("User should register");
        }
        if(!existingUser.isActive()) {
            return ResponseEntity.badRequest().body("Your account has been deactivated");
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

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody UserForgetPasswordRequest userForgetPasswordRequest) {
        String email = userForgetPasswordRequest.getEmail();
        Boolean isRequestAllowed = rateLimiterService.isAllowed(email);
        if(!isRequestAllowed) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many requests. Try again later.");
        }
        userService.forgetPassword(email);
        return ResponseEntity.ok("An email has been sent to your email to reset the password");

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody UserResetPasswordRequest userResetPasswordRequest) {
        userService.resetPassword(userResetPasswordRequest.getToken(), userResetPasswordRequest.getPassword());
        return ResponseEntity.ok("Password has been reset successfully");

    }
    @GetMapping("/reset-password")
    public ResponseEntity<?> getResetPassword(@RequestParam String token) {
        return ResponseEntity.ok("Password has been reset successfully token: "+token);

    }

    @PostMapping("/deactivate-user")
    public ResponseEntity<?> deactivateUser(@RequestParam Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok("User has been deactivated successfully");

    }

    @PostMapping("/activate-user")
    public ResponseEntity<?> activateUser(@RequestParam Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok("User has been reactivated successfully");

    }

    @GetMapping("/get-user-details")
    public ResponseEntity<?> getUserDetails(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        UserDisplayDTO userDisplayDTO = userService.getUserDetails(user);
        return ResponseEntity.ok(userDisplayDTO);

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
