package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {

    @NotNull(message = "Role cannot be empty")
    private String role;
}
