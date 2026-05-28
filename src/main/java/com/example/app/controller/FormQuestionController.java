package com.example.app.controller;

import com.example.app.dto.project.*;
import com.example.app.dto.question.FormQuestionDTO;
import com.example.app.entity.*;
import com.example.app.entity.status.ProjectStatus;
import com.example.app.entity.status.UserRoles;
import com.example.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/formquestion")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FormQuestionController {

    private final FormQuestionRepository formQuestionRepository;

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('CLIENT') || hasRole('TECH_TEAM') || hasRole('ADMIN')")
    public ResponseEntity getAll(@AuthenticationPrincipal User logged) {

        List<FormQuestion> questionList = new ArrayList<>();
        List<FormQuestionDTO> questionDTOList = new ArrayList<>();

        if (logged.getRole() == UserRoles.ADMIN || logged.getRole() == UserRoles.TECH_TEAM){
            questionList = formQuestionRepository.findAll();
        } else if (logged.getRole() == UserRoles.CLIENT){
            questionList = formQuestionRepository.findSelected();
        } else {
            ResponseEntity.status(400).body("There was an error getting all Answers!");
        }

        for( FormQuestion question : questionList){
            questionDTOList.add(new FormQuestionDTO(question.getId(), question.getQuestion()));
        }

        return ResponseEntity.ok(questionDTOList);
    }

}
