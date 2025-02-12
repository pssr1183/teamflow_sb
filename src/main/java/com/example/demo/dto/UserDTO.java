package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class UserDTO implements Serializable {

    private Long id;
    private String username;
    private Set<String> rolenames;

}
