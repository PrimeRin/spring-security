package com.example.spring_security.auth.api.v1.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
