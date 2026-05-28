package com.example.app.dto.auth;

public record UserListDTO(Long id, String username, String email, String role, Boolean status) {
}
