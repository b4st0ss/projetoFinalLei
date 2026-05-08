package com.example.app.controller;

import com.example.app.dto.feedback.FeedbackRequestDTO;
import com.example.app.dto.project.CreateProjectDTO;
import com.example.app.entity.*;
import com.example.app.entity.status.ProjectStatus;
import com.example.app.entity.status.UserRoles;
import com.example.app.repository.FeedbackRepository;
import com.example.app.repository.ProjectAnswerRepository;
import com.example.app.repository.RequirementRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FeedbackController {

    private final RequirementRepository requirementRepository;
    private final ProjectAnswerRepository projectAnswerRepository;
    private final FeedbackRepository feedbackRepository;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TECH_TEAM', 'CLIENT')")
    public ResponseEntity create(@RequestBody FeedbackRequestDTO body, @AuthenticationPrincipal User logged){

        if (body.message() == null) { return ResponseEntity.status(400).body("Message can't be null!"); }
        if (body.requirementId() ==  null && body.answerId() == null){ return ResponseEntity.status(400).body("Requirement/Answer not associated!");}
        if (body.requirementId() != null && body.answerId() != null) { return ResponseEntity.status(400).body("Feedback cannot be associated with both a Requirement and an Answer simultaneously!"); }

        Feedback feedback = new Feedback();
        feedback.setUser(logged);
        feedback.setMessage(body.message());

        if (body.requirementId() != null) {
            Optional<Requirement> optionalRequirement = requirementRepository.findById(body.requirementId());
            if (optionalRequirement.isEmpty()) { return ResponseEntity.status(404).body("Requirement not found!"); }
            Requirement requirement = optionalRequirement.get();
            feedback.setRequirement(requirement);
        }
        if (body.answerId() != null) {
            Optional<ProjectAnswer> optionalProjectAnswer = projectAnswerRepository.findById(body.answerId());
            if (optionalProjectAnswer.isEmpty()) { return ResponseEntity.status(404).body("Project Answer not found!"); }
            ProjectAnswer projectAnswer = optionalProjectAnswer.get();
            feedback.setProjectAnswer(projectAnswer);
        }

        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback Submitted Successfullyy");
    }
}

