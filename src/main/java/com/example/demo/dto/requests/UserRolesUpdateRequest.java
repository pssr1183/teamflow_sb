package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserRolesUpdateRequest {

    @NotNull
    private Set<String> rolenames;
}
