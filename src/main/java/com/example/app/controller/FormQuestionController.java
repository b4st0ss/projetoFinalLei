package com.example.app.controller;

import com.example.app.dto.project.*;
import com.example.app.dto.question.FormQuestionDTO;
import com.example.app.dto.response.ResponseDTO;
import com.example.app.entity.*;
import com.example.app.entity.status.ProjectStatus;
import com.example.app.entity.status.UserRoles;
import com.example.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
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
            questionDTOList.add(new FormQuestionDTO(question.getId(), question.getQuestion(), question.getSelected()));
        }

        return ResponseEntity.ok(questionDTOList);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create(@RequestBody FormQuestionDTO body){

        FormQuestion newQuestion = new FormQuestion();
        newQuestion.setQuestion(body.question());
        newQuestion.setSelected(true);
        formQuestionRepository.save(newQuestion);

        return ResponseEntity.ok(new ResponseDTO("Question created successfully"));
    }

    @PostMapping("/select/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity select(@PathVariable Long questionId){

        Optional<FormQuestion> optionalFormQuestion = formQuestionRepository.findById(questionId);

        if (optionalFormQuestion.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("Question not found"));
        }

        FormQuestion question = optionalFormQuestion.get();

        boolean state = question.getSelected();
        question.setSelected(!state);
        formQuestionRepository.save(question);

        return ResponseEntity.ok(new ResponseDTO("Question status updated successfully"));
    }
}
