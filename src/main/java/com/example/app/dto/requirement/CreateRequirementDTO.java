package com.example.app.dto.requirement;

import com.example.app.entity.Requirement;

import java.util.List;

public record CreateRequirementDTO(long projectId, List<RequirementItemDTO> requirements) {
}
