package com.example.app.dto.feedback;

public record FeedbackRequestDTO(Long requirementId, Long answerId, String message) {
}
