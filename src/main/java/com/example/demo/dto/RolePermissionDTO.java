package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RolePermissionDTO {
    private String roleName;
    private Set<String> permissions;
}
