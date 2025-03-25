package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResetPasswordRequest {

    @NotEmpty(message = "token cannot be empty")
    private String token;

    @NotEmpty(message = "password cannot be empty")
    private String password;
}
