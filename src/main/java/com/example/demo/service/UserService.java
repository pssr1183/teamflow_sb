package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String displayName, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        return userRepository.save(user);
    }

    public User findByUsername(String username) {

        return userRepository.findByUsername(username).orElse(null);
    }

    public Map<String,Set<String >> findAllUsers() {

        List<User> users = userRepository.findAll();
        Map<String,Set<String >> usersMap = users.stream().collect(Collectors.toMap(
                User::getUsername,
                user -> user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        ));

        return usersMap;
    }

    public Set<Permission> userPermissions(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Permission> permissions = user.getRoles().stream().flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.toSet());
        return permissions;

    }

    public Map<String, Set<String>> getUserRoles() {

        Set<Role> roleSet = new HashSet<>();
        roleSet.addAll(roleRepository.findAll());
        Map<String, Set<String>> rolePermissionsMap = roleSet.stream().collect(Collectors.toMap(
                Role::getName,
                role -> role.getPermissions().stream().map(permission -> permission.getName()).collect(Collectors.toSet())
        ));

        return rolePermissionsMap;
    }

    public Map<String, Set<String>> getCurrentUserRoles(User user) {

        Map<String, Set<String>> rolePermissionsMap = user.getRoles().stream().collect(Collectors.toMap(
                Role::getName,
                role -> role.getPermissions().stream().map(permission -> permission.getName()).collect(Collectors.toSet())
        ));

        return rolePermissionsMap;
    }

    public boolean canPerformAny(User currentUser, String currentPermission) {

       return currentUser.getRoles().isEmpty() &&  currentUser.getRoles().stream().flatMap(role -> role.getPermissions().stream()).anyMatch(permission -> permission.getName().equalsIgnoreCase(currentPermission));
    }
}
