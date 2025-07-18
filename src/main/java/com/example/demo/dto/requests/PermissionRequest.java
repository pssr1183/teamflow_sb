package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PermissionRequest {

    @NotNull(message = "Permission cannot be empty")
    private String permission;
}
