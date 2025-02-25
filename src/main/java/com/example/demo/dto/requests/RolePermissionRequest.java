package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RolePermissionRequest {

    @NotEmpty(message = "Title cannot be empty")
    private String roleName;

    @NotNull(message = "Permissions cannot be empty")
    private Set<String> permissions;
}
