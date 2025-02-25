package com.example.demo.dto.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserRegistrationRequest extends UserRequest {
    private String displayName;
    private Set<String> rolenames;
}
