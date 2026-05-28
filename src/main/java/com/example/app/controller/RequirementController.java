package com.example.app.controller;

import com.example.app.dto.project.CreateProjectDTO;
import com.example.app.dto.requirement.CreateRequirementDTO;
import com.example.app.dto.requirement.RequirementItemDTO;
import com.example.app.dto.response.ResponseDTO;
import com.example.app.entity.Project;
import com.example.app.entity.Requirement;
import com.example.app.entity.User;
import com.example.app.entity.status.ProjectStatus;
import com.example.app.entity.status.RequirementStatus;
import com.example.app.repository.ProjectRepository;
import com.example.app.repository.RequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/requirement")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RequirementController {

    private final ProjectRepository projectRepository;
    private final RequirementRepository requirementRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity create(@RequestBody CreateRequirementDTO body, @AuthenticationPrincipal User logged){

        Optional<Project> optionalProject = projectRepository.findById(body.projectId());

        if (optionalProject.isEmpty()){
            return ResponseEntity.status(404).body(new ResponseDTO("Project not found!"));
        }

        Project project = optionalProject.get();

        if (project.getClient().getId() != logged.getId()) {
            return ResponseEntity.status(403).body(new ResponseDTO("Don't have permission to access this project"));
        }
        else if (project.getStatus() == ProjectStatus.UNDER_TECHNICAL_REVIEW || project.getStatus() == ProjectStatus.COMPLETED){
            return ResponseEntity.status(400).body(new ResponseDTO("This project is already completed or under review"));
        }

        for (RequirementItemDTO requirementDTO : body.requirements()){
            Requirement requirement = new Requirement();
            requirement.setTitle(requirementDTO.title());
            requirement.setDescription(requirementDTO.description());
            requirement.setProject(project);
            requirement.setUser(logged);
            requirement.setStatus(RequirementStatus.PENDING_REVIEW);

            requirementRepository.save(requirement);
        }

        return ResponseEntity.ok( new ResponseDTO("Requirement Created Successfully"));
    }



}
