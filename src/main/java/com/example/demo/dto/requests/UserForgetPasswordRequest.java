package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserForgetPasswordRequest {

    @NotEmpty(message = "email cannot be empty")
    private String email;
}
