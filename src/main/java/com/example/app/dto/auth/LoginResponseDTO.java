package com.example.app.dto.auth;

import com.example.app.entity.status.UserRoles;

public record LoginResponseDTO(String name, UserRoles role, String token) {
}
