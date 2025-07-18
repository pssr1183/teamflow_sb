package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class UserDTO implements Serializable {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String username;
    private Set<String> rolenames;

}
