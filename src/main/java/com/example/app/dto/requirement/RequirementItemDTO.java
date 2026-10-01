package com.example.app.dto.requirement;

public record RequirementItemDTO(Long requirementId,
                                 String title,
                                 String description,
                                 String status,
                                 Long categoryId,
                                 Integer position
) { }
