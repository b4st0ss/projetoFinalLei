package com.example.app.controller;

import com.example.app.dto.feedback.FeedbackRequestDTO;
import com.example.app.dto.feedback.FeedbackResponseDTO;
import com.example.app.dto.project.CreateProjectDTO;
import com.example.app.dto.response.ResponseDTO;
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

import java.util.ArrayList;
import java.util.List;
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
    @PreAuthorize("hasRole('CLIENT') || hasRole('TECH_TEAM') || hasRole('ADMIN')")
    public ResponseEntity create(@RequestBody FeedbackRequestDTO body, @AuthenticationPrincipal User logged){

        if (body.message() == null) {
            return ResponseEntity.status(400).body(new ResponseDTO("Message can't be null!"));
        }
        if (body.requirementId() ==  null && body.answerId() == null){
            return ResponseEntity.status(400).body(new ResponseDTO("Requirement/Answer not associated!"));
        }
        if (body.requirementId() != null && body.answerId() != null) {
            return ResponseEntity.status(400).body(new ResponseDTO("Feedback cannot be associated with both a Requirement and an Answer simultaneously!"));
        }

        Feedback feedback = new Feedback();
        feedback.setUser(logged);
        feedback.setMessage(body.message());

        if (body.requirementId() != null) {
            Optional<Requirement> optionalRequirement = requirementRepository.findById(body.requirementId());
            if (optionalRequirement.isEmpty()) {
                return ResponseEntity.status(404).body(new ResponseDTO("Requirement not found!"));
            }
            Requirement requirement = optionalRequirement.get();
            feedback.setRequirement(requirement);
        }
        if (body.answerId() != null) {
            Optional<ProjectAnswer> optionalProjectAnswer = projectAnswerRepository.findById(body.answerId());
            if (optionalProjectAnswer.isEmpty()) {
                return ResponseEntity.status(404).body(new ResponseDTO("Project Answer not found!"));
            }
            ProjectAnswer projectAnswer = optionalProjectAnswer.get();
            feedback.setProjectAnswer(projectAnswer);
        }

        feedbackRepository.save(feedback);
        return ResponseEntity.ok(new ResponseDTO("Feedback Submitted Successfullyy"));
    }

    @GetMapping("/answer/{answerId}")
    @PreAuthorize("hasRole('CLIENT') || hasRole('TECH_TEAM') || hasRole('ADMIN')")
    public ResponseEntity getAnswerFeedbacks(@PathVariable Long answerId){

        List<Feedback> feedbackList = feedbackRepository.findFeedbackByAnswerId(answerId);
        List<FeedbackResponseDTO> feedbackDTOList = new ArrayList<>();

        for (Feedback feedback : feedbackList){
            FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO(feedback.getId(), feedback.getMessage(), feedback.getUser().getUsername(), feedback.getUser().getRole().toString(), feedback.getCreation_data().toString());
            feedbackDTOList.add(feedbackResponseDTO);
        }

        return ResponseEntity.ok(feedbackDTOList);
    }

    @GetMapping("/requirement/{requirementId}")
    @PreAuthorize("hasRole('CLIENT') || hasRole('TECH_TEAM') || hasRole('ADMIN')")
    public ResponseEntity getRequirementFeedbacks(@PathVariable Long requirementId){

        List<Feedback> feedbackList = feedbackRepository.findFeedbackByRequirementId(requirementId);
        List<FeedbackResponseDTO> feedbackDTOList = new ArrayList<>();

        for (Feedback feedback : feedbackList){
            FeedbackResponseDTO feedbackResponseDTO = new FeedbackResponseDTO(feedback.getId(), feedback.getMessage(), feedback.getUser().getUsername(), feedback.getUser().getRole().toString(), feedback.getCreation_data().toString());
            feedbackDTOList.add(feedbackResponseDTO);
        }

        return ResponseEntity.ok(feedbackDTOList);
    }




}

