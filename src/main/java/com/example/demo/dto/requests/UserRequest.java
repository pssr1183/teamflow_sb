package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserRequest {

    @NotEmpty(message = "UserName cannot be empty")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
