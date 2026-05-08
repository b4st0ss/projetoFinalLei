package com.example.app.dto.project;

import java.util.List;

public record AssociateTechTeamDTO(Long projectId, List<Long> techId) {
}
