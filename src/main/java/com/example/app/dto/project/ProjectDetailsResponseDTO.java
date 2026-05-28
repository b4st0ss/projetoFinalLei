package com.example.app.dto.project;

import com.example.app.dto.answer.AnswerItemDTO;
import com.example.app.dto.requirement.RequirementItemDTO;

import java.util.List;

public record ProjectDetailsResponseDTO(long projectId, String status, List<AnswerItemDTO> answers, List<RequirementItemDTO> requirements) {}
