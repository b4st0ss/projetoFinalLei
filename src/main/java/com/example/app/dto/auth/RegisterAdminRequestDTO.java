package com.example.app.dto.auth;

public record RegisterAdminRequestDTO(String name, String username, String email, String password, String role) {
}

