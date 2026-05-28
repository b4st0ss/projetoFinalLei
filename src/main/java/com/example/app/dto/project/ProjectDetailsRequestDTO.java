package com.example.app.dto.project;

import com.example.app.dto.answer.AnswerItemDTO;
import com.example.app.dto.requirement.RequirementItemDTO;

import java.util.List;

public record ProjectDetailsRequestDTO(Long projectId, List<AnswerItemDTO> answers, List<RequirementItemDTO> requirements, Boolean draft) {
}
