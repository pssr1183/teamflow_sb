package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserRequest {
    private String username;
    private String displayName;
    private String password;
    private Set<String> rolenames;
}
