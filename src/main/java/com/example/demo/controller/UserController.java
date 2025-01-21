package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if(userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("User is already present");
        }
        userService.register(user.getUsername(),user.getPassword(),"USER_ROLE");
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
        return ResponseEntity.ok("User has been successfully Logged In"+username+" "+role);
    }
}
