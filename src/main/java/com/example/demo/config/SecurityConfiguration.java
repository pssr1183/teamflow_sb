package com.example.demo.config;

import com.example.demo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Set;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection (not recommended for production)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/register","/api/auth/login","/api/auth/permissions").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/tasks/**").authenticated()
                        .anyRequest().authenticated()          // Require authentication for other endpoints
                )
                .httpBasic(Customizer.withDefaults()); // Updated HTTP Basic authentication configuration

        return http.build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        // Define an in-memory user
//        return new InMemoryUserDetailsManager(
//                User.builder()
//                        .username("user")
//                        .password(passwordEncoder().encode("passwords"))
//                        .roles("USER")
//                        .build(),
//                User.builder()
//                        .username("user2")
//                        .password(passwordEncoder().encode("password2"))
//                        .roles("ADMIN")
//                        .build()
//        );
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)  // Configure UserDetailsService
                .passwordEncoder(passwordEncoder())  // Set PasswordEncoder
                .and().build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return username -> {
            com.example.demo.entity.User user = userService.findByUsername(username); // Find user from database
            if (user != null) {
                Set<String> authoritiesSet = new HashSet<>();
                authoritiesSet.add("ROLE_"+user.getRole().getName());
                System.out.println(authoritiesSet.toString()+" "+username);
                user.getRole().getPermissions().forEach(p -> authoritiesSet.add(p.getName()));
//                System.out.println(authoritiesSet.toString());
                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(), user.getPassword(),
                        AuthorityUtils.createAuthorityList(
                                authoritiesSet.toArray(new String[0]) // Pass all authorities
                        )
                );
            }
            throw new UsernameNotFoundException("User not found");
        };
    }
}