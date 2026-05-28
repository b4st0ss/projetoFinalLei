package com.example.app.dto.feedback;

public record FeedbackResponseDTO(long id, String message, String username, String role, String creationData) {
}

