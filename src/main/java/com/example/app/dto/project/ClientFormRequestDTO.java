package com.example.app.dto.project;

import java.util.List;

public record ClientFormRequestDTO(Long projectId, List<ClientProjectAnswerDTO> answers) {
}
