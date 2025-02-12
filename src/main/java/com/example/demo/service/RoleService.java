package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.exceptions.RoleNotFoundException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service

public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Set<Permission> setPermissions(String roleName, Set<Permission> permissions) {
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()-> new RoleNotFoundException("Role not found with name "+roleName)
        );
        role.setPermissions(permissions);
        roleRepository.save(role);
        return role.getPermissions();
    }
}
