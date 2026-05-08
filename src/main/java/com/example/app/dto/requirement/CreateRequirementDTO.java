package com.example.app.dto.requirement;

import com.example.app.entity.Requirement;

import java.util.List;

public record CreateRequirementDTO(Long projectId, List<RequirementItemDTO> requirements) {
}
