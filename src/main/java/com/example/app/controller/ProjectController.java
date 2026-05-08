package com.example.app.controller;

import com.example.app.dto.project.*;
import com.example.app.entity.*;
import com.example.app.entity.status.ProjectStatus;
import com.example.app.entity.status.RequirementStatus;
import com.example.app.entity.status.UserRoles;
import com.example.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FormQuestionRepository formQuestionRepository;
    private final ProjectAnswerRepository projectAnswerRepository;
    private final TechProjectRepository techProjectRepository;


    // Admin create the project
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create(@RequestBody CreateProjectDTO body, @AuthenticationPrincipal User logged){
        Optional<User> clientOptional = userRepository.findById(body.clientId());

        // Checking if the user exists
        if (clientOptional.isEmpty()) { return ResponseEntity.status(404).body("Client not found"); }

        User client = clientOptional.get();

        // Checking user roles
        if (client.getRole() != UserRoles.CLIENT) { return ResponseEntity.status(400).body("Must be a client!"); }

        Project newProject = new Project();
        newProject.setTitle(body.title());
        newProject.setDescription(body.description());
        newProject.setClient(client);
        newProject.setStatus(ProjectStatus.DRAFT);
        projectRepository.save(newProject);

        return ResponseEntity.ok("Project Created Successfully");
    }


    // Client fill the form
    @PostMapping("/clientForm")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity clientForm(@RequestBody ClientFormRequestDTO body, @AuthenticationPrincipal User logged) {
        Optional<Project> projectOptional = projectRepository.findById(body.projectId());

        if (projectOptional.isEmpty()) { return ResponseEntity.status(404).body("Project not found"); }

        Project project = projectOptional.get();

        if (project.getClient().getId() != logged.getId()) { return ResponseEntity.status(403).body("Don't have permission to access this project");}
        else if (project.getStatus() == ProjectStatus.UNDER_TECHNICAL_REVIEW || project.getStatus() == ProjectStatus.COMPLETED){
            return ResponseEntity.status(400).body("This project is already completed or under review");
        }

        for (ClientProjectAnswerDTO answer : body.answers()){
            Optional<FormQuestion> questionOptional = formQuestionRepository.findById(answer.formQuestionId());

            if (questionOptional.isEmpty()){ return ResponseEntity.status(404).body("Question with the id: " + answer.formQuestionId() + "was not found"); }

            FormQuestion question = questionOptional.get();

            ProjectAnswer projectAnswer = new ProjectAnswer();
            projectAnswer.setProject(project);
            projectAnswer.setFormQuestion(question);
            projectAnswer.setUser(logged);
            projectAnswer.setAnswer(answer.answerText());
            projectAnswer.setStatus(RequirementStatus.PENDING_REVIEW);

            // Saving project Answer
            projectAnswerRepository.save(projectAnswer);
        }

        // Changing project Status
        project.setStatus(ProjectStatus.UNDER_TECHNICAL_REVIEW);
        projectRepository.save(project);

        return ResponseEntity.ok("Form submitted succesfully!");
    }


    // Admin associate Tecs to the project
    @PostMapping("/associateTech")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity associateTech(@RequestBody AssociateTechTeamDTO body, @AuthenticationPrincipal User logged){
        Optional<Project> projectOptional = projectRepository.findById(body.projectId());

        if (projectOptional.isEmpty()) { return ResponseEntity.status(404).body("Project not found");}

        Project project = projectOptional.get();

        if (project.getStatus() == ProjectStatus.COMPLETED) { return ResponseEntity.status(400).body("Can't associate Tech Team to a completed Project");}

        for (Long techId : body.techId()) {
            Optional<User> optionalUser = userRepository.findById(techId);

            if (optionalUser.isEmpty()){ return ResponseEntity.status(404).body("Tech not Found"); }

            User tech = optionalUser.get();

            if (tech.getRole() != UserRoles.TECH_TEAM) { return ResponseEntity.status(400).body("This user with id: " + techId + " isn't on the TECH_TEAM");}

            Tech_Project techProject = new Tech_Project();
            techProject.setProject(project);
            techProject.setDev(tech);
            techProject.setAdmin(logged);

            techProjectRepository.save(techProject);
        }

        if (body.techId().size() > 1){ return ResponseEntity.ok("Tech associated successfully!");}

        return ResponseEntity.ok("TECH_TEAM associated successfully");
    }


    // Tech team changes the project status to MORE_INFO_NEEDDED
    @PostMapping("/moreInfo")
    @PreAuthorize("hasRole('TECH_TEAM')")
    public ResponseEntity moreInfo(@RequestBody MoreInfoNeededDTO body, @AuthenticationPrincipal User logged) {
        Optional<Project> optionalProject = projectRepository.findById(body.projectId());

        if (optionalProject.isEmpty()){ return  ResponseEntity.status(404).body("Project not found!"); }

        Project project = optionalProject.get();

        project.setStatus(ProjectStatus.MORE_INFO_NEEDED);
        projectRepository.save(project);

        return ResponseEntity.ok("Project Status Changed! More info needed!");
    }
}

