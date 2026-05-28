package com.example.app.controller;

import com.example.app.dto.answer.AnswerItemDTO;
import com.example.app.dto.auth.UserItemDTO;
import com.example.app.dto.project.*;
import com.example.app.dto.requirement.RequirementItemDTO;
import com.example.app.dto.response.ResponseDTO;
import com.example.app.entity.*;
import com.example.app.entity.status.ProjectStatus;
import com.example.app.entity.status.RequirementStatus;
import com.example.app.entity.status.UserRoles;
import com.example.app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    private final RequirementRepository requirementRepository;


    // Admin create the project
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity create(@RequestBody CreateProjectDTO body){
        Optional<User> clientOptional = userRepository.findByEmail(body.email());

        // Checking if the user exists
        if (clientOptional.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("Client not found"));
        }

        User client = clientOptional.get();

        // Checking user roles
        if (client.getRole() != UserRoles.CLIENT) {
            return ResponseEntity.status(400).body(new ResponseDTO("Must be a client!"));
        }

        Project newProject = new Project();
        newProject.setTitle(body.title());
        newProject.setDescription(body.description());
        newProject.setClient(client);
        newProject.setStatus(ProjectStatus.WAITING_FOR_CLIENT);
        projectRepository.save(newProject);

        return ResponseEntity.ok(new ResponseDTO("Project Created Successfully"));
    }


    // Client fill the form
    @Transactional
    @PostMapping("/clientForm")
    @PreAuthorize("hasRole('CLIENT') || hasRole('TECH_TEAM') || hasRole('ADMIN')")
    public ResponseEntity clientForm(@RequestBody ProjectDetailsRequestDTO body, @AuthenticationPrincipal User logged) {
        Optional<Project> projectOptional = projectRepository.findById(body.projectId());

        if (projectOptional.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("Project not found"));
        }

        Project project = projectOptional.get();

        if (logged.getRole() == UserRoles.CLIENT && project.getClient().getId() != logged.getId()) {
            return ResponseEntity.status(403).body(new ResponseDTO("You don't have permission to access this project"));
        }


        switch (project.getStatus()) {
            case DRAFT:
            case WAITING_FOR_CLIENT:
            case MORE_INFO_NEEDED:
                if (logged.getRole() == UserRoles.TECH_TEAM || logged.getRole() == UserRoles.ADMIN) {
                    return ResponseEntity.status(403).body(new ResponseDTO("Staff can't change the project right now"));
                }
                break;

            case UNDER_TECHNICAL_REVIEW:
                if (logged.getRole() == UserRoles.CLIENT) {
                    return ResponseEntity.status(403).body(new ResponseDTO("Project is under Technical Team review, you cannot change data right now!"));
                }
                break;

            case COMPLETED:
                // Project finished
                return ResponseEntity.status(400).body(new ResponseDTO("Project Finished! No more changes allowed!"));

            default:
                return ResponseEntity.status(400).body(new ResponseDTO("Invalid project status!"));
        }

        if(body.answers() != null) {
            for (AnswerItemDTO answer : body.answers()) {
                Optional<ProjectAnswer> projectAnswerOptional = projectAnswerRepository.findProjectAnserByProjectAndFormQuestion(project.getId(), answer.formQuestionId());

                if (projectAnswerOptional.isPresent()) {
                    ProjectAnswer answerPresent = projectAnswerOptional.get();
                    answerPresent.setAnswer(answer.answerText());
                    answerPresent.setUser(logged);
                    answerPresent.setStatus(RequirementStatus.PENDING_REVIEW);
                    projectAnswerRepository.save(answerPresent);
                } else {
                    Optional<FormQuestion> questionOptional = formQuestionRepository.findById(answer.formQuestionId());

                    if (questionOptional.isEmpty()){
                        return ResponseEntity.status(404).body(new ResponseDTO("Question with the id:" + answer.formQuestionId() + " was not found"));
                    }

                    FormQuestion question = questionOptional.get();

                    ProjectAnswer projectAnswer = new ProjectAnswer();
                    projectAnswer.setProject(project);
                    projectAnswer.setFormQuestion(question);
                    projectAnswer.setUser(logged);
                    projectAnswer.setAnswer(answer.answerText());
                    projectAnswer.setStatus(RequirementStatus.PENDING_REVIEW);

                    projectAnswerRepository.save(projectAnswer);
                }
            }

            if (body.requirements() != null) {
                for (RequirementItemDTO requirement : body.requirements()) {
                    Optional<Requirement> existingReqOpt = requirementRepository.findByProjectIdAndTitle(project.getId(), requirement.title());

                    if (existingReqOpt.isPresent()) {
                        Requirement existingReq = existingReqOpt.get();
                        existingReq.setDescription(requirement.description());
                        existingReq.setUser(logged);
                        existingReq.setStatus(RequirementStatus.PENDING_REVIEW);
                        requirementRepository.save(existingReq);
                    } else {
                        Requirement newRequirement = new Requirement();
                        newRequirement.setProject(project);
                        newRequirement.setUser(logged);
                        newRequirement.setTitle(requirement.title());
                        newRequirement.setDescription(requirement.description());
                        newRequirement.setStatus(RequirementStatus.PENDING_REVIEW);

                        requirementRepository.save(newRequirement);
                    }
                }
            }
        }


        String responseMessage;

        if(logged.getRole() == UserRoles.CLIENT){
            if (body.draft()) {
                project.setStatus(ProjectStatus.DRAFT);
                responseMessage = "Draft updated successfully!";
            } else {
                project.setStatus(ProjectStatus.UNDER_TECHNICAL_REVIEW);
                responseMessage = "Form submitted successfully!";
            }
        } else {
            responseMessage = "Project data updated by staff!";
        }

        projectRepository.save(project);
        return ResponseEntity.ok(new ResponseDTO(responseMessage));
    }


    // Admin associate Tecs to the project
    @Transactional
    @PostMapping("/associateTech")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity associateTech(@RequestBody AssociateTechDTO body, @AuthenticationPrincipal User logged){
        Optional<Project> projectOptional = projectRepository.findById(body.projectId());
        if (projectOptional.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("Project not found"));
        }
        Project project = projectOptional.get();

        if (project.getStatus() == ProjectStatus.COMPLETED) {
            return ResponseEntity.status(400).body(new ResponseDTO("Can't associate Tech Team to a completed Project"));
        }

        Optional<User> optionalUser = userRepository.findByEmail(body.email());
        if (optionalUser.isEmpty()){
            return ResponseEntity.status(404).body(new ResponseDTO("User with email " + body.email() + " not found"));
        }
        User tech = optionalUser.get();

        if (tech.getRole() != UserRoles.TECH_TEAM) {
            return ResponseEntity.status(400).body(new ResponseDTO("The user with email " + body.email() + " isn't on the TECH_TEAM"));
        }

        Optional<Tech_Project> associated = techProjectRepository.findByProjectIdAndTechId(project.getId(), tech.getId());
        if (associated.isPresent()) {
            return ResponseEntity.status(400).body(new ResponseDTO("This tech is already associated with this project!"));
        }

        Tech_Project techProject = new Tech_Project();
        techProject.setProject(project);
        techProject.setTech(tech);
        techProject.setAdmin(logged);
        techProjectRepository.save(techProject);

        return ResponseEntity.ok(new ResponseDTO("Tech associated successfully!"));
    }


    @Transactional
    @DeleteMapping("/disassociateTech")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity disassociateTech(@RequestBody DisassociateTechDTO body){

        techProjectRepository.deleteByProjectIdAndTechId(body.projectId(), body.techId());

        return ResponseEntity.ok(new ResponseDTO("Tech disassociated successfully!"));
    }





    // Tech team changes the project status to MORE_INFO_NEEDDED
    @PostMapping("/moreInfo")
    @PreAuthorize("hasRole('TECH_TEAM')")
    public ResponseEntity moreInfo(@RequestBody MoreInfoNeededDTO body, @AuthenticationPrincipal User logged) {
        Optional<Project> optionalProject = projectRepository.findById(body.projectId());

        if (optionalProject.isEmpty()){
            return  ResponseEntity.status(404).body(new ResponseDTO("Project not found!"));
        }

        Project project = optionalProject.get();

        project.setStatus(ProjectStatus.MORE_INFO_NEEDED);
        projectRepository.save(project);

        return ResponseEntity.ok(new ResponseDTO("Project Status Changed! More info needed!"));
    }




    @GetMapping("/{projectId}/getTechs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getAssociated(@PathVariable Long projectId){

        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()){
            return ResponseEntity.status(404).body(new ResponseDTO("Project not found"));
        }

        List<Tech_Project> techProjectList = techProjectRepository.findByProjectId(projectId);
        List<UserItemDTO> techsList = new ArrayList<>();

        for (Tech_Project techProject : techProjectList){
            User tech = techProject.getTech();
            techsList.add(new UserItemDTO(tech.getId(), tech.getName(), tech.getEmail()));
        }

        return ResponseEntity.ok(techsList);
    }





    @GetMapping("/getProjects")
    public ResponseEntity<?> getprojects(@AuthenticationPrincipal User logged){
        if (logged.getRole() == UserRoles.ADMIN){
            return ResponseEntity.ok(projectRepository.findNotCompleted());
        }

        if (logged.getRole() == UserRoles.CLIENT) {
            return ResponseEntity.ok(projectRepository.findByClientId(logged.getId()));
        }

        if (logged.getRole() == UserRoles.TECH_TEAM){
            return ResponseEntity.ok(projectRepository.findByTechProjectId(logged.getId()));
        }

        return ResponseEntity.ok("Projects Not Found");
    }



    @GetMapping("/{projectId}/details")
    public ResponseEntity<?> getProjectDetails(@PathVariable Long projectId, @AuthenticationPrincipal User logged) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Project not found");
        }

        Project project = projectOptional.get();

//        if (logged.getRole() == UserRoles.CLIENT && project.getClient().getId() != logged.getId()) {
//            return ResponseEntity.status(403).body("Access denied");
//        }

        List<AnswerItemDTO> answersList = new ArrayList<>(); // array of dto's to send to front
        List<ProjectAnswer>  answersListDB = projectAnswerRepository.findByProjectId(projectId); // array of answers from db

        // Catch Answers
        for (ProjectAnswer answer : answersListDB) {
            AnswerItemDTO answerDTO = new AnswerItemDTO(answer.getId(),answer.getFormQuestion().getId(), answer.getFormQuestion().getQuestion(), answer.getAnswer(),answer.getStatus().toString());
            answersList.add(answerDTO);
        }


        List<RequirementItemDTO> requirementsList = new ArrayList<>();
        List<Requirement> requirementsListDB = requirementRepository.findByProjectId(projectId);

        // Catch Requirements
        for (Requirement requirement : requirementsListDB) {
            RequirementItemDTO requirementDTO = new RequirementItemDTO(requirement.getId(),requirement.getTitle(), requirement.getDescription(),requirement.getStatus().toString());
            requirementsList.add(requirementDTO);
        }

        // DTO do send to front
        ProjectDetailsResponseDTO projectDetailsResponseDTO = new ProjectDetailsResponseDTO(projectId, project.getStatus().toString(), answersList, requirementsList);
        return ResponseEntity.ok(projectDetailsResponseDTO);
    }

}

