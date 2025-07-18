package com.example.demo.service;

import com.example.demo.config.messageConfig.EmailMessageBody;
import com.example.demo.dto.UserDisplayDTO;
import com.example.demo.entity.*;
import com.example.demo.exceptions.*;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Autowired
    private EmailMessageBody messageBody;

    @Autowired
    private NotificationService notificationService;

    public User register(String username, String displayName, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken");
        }

        userRepository.save(user);
        String message = messageBody.getUserRegistrationBody(username,password,displayName);
        notificationService.sendEmailNotification(new UserNotification(displayName,message, username,password, Notification.NotificationType.USER_REGISTERED));

        return user;
    }

    public User findByUsername(String username) {

        return userRepository.findByUsername(username).orElse(null);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID: "+userId));
    }

    public Map<Long, Map<String, Object >> findAllUsers() {

        List<User> users = userRepository.findAll();
        Map<Long, Map<String, Object >> usersMap = users.stream().collect(Collectors.toMap(
                User::getId,
                user ->
                    Map.of(
                            "username",user.getUsername(),
                            "roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
                    )
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

    public User updateRoles(User user, Set<Role> vaildRoles) {
        user.setRoles(vaildRoles);
        userRepository.save(user);
        return user;
    }

    public void forgetPassword(String email) {

        User user = userRepository.findByUsername(email).orElseThrow(()-> new UserNotFoundException("The following user doesn't exists"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String message = messageBody.getPasswordResetBody(user.getDisplayName(),"http://localhost:8080/api/auth/reset-password?token="+token);
        notificationService.sendEmailNotification(new UserNotification(user.getDisplayName(),message, user.getUsername(), null,Notification.NotificationType.USER_RESET_PASSWORD));
    }

    public void resetPassword(String token, String password ) {
        System.out.println(token+" "+password);
        User user = userRepository.findByResetToken(token).orElseThrow(()-> new TokenExpiredException("The Reset Password Token is invalid"));
        if(user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Password Reset Token expired");
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("The following user doesn't exists"));
        if(!user.isActive()) throw new UserAlreadyDeactivatedException("User is already deactivated");
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("The following user doesn't exists"));
        if(user.isActive()) throw new UserAlreadyActivatedException("User is already active");
        user.setActive(true);
        userRepository.save(user);
    }

    public boolean isUserActive(User user) {
        if(!user.isActive()) {
           return false;
        }
        return true;
    }

    public UserDisplayDTO getUserDetails(User user) {
        UserDisplayDTO userDisplayDTO = new UserDisplayDTO();
        userDisplayDTO.setDisplayName(user.getDisplayName());
        userDisplayDTO.setUsername(user.getUsername());
        userDisplayDTO.setRolenames(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return userDisplayDTO;
    }
}
