package com.example.app.controller;

import com.example.app.dto.service.ServiceRequestDTO;
import com.example.app.entity.Service;
import com.example.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requirement")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
public class RequirementController {

    @PostMapping("/createSpecial")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity createSpecial(@RequestBody ServiceRequestDTO body, @AuthenticationPrincipal User logged){

        // var authentication = SecurityContextHolder.getContext().getAuthentication();
        // User user = (User) authentication.getPrincipal();

        Service newService = new Service();
        newService.setUser_id(logged.getId());
        newService.setTitle(body.title());
        newService.setDescription(body.description());
        newService.setStatus("STANDBY");
        serviceRepository.save(newService);

        return ResponseEntity.ok("Service Created Successfully");
    }
}
