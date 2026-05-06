package com.example.app.controller;

import com.example.app.dto.ServiceRequestDTO;
import com.example.app.entity.Service;
import com.example.app.entity.User;
import com.example.app.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ServiceRequestDTO body, @AuthenticationPrincipal User logged){

        Service newService = new Service();
        newService.setUser_id(logged.getId());
        newService.setTitle(body.title());
        newService.setDescription(body.description());
        newService.setStatus("STARTED");
        serviceRepository.save(newService);

        return ResponseEntity.ok("Service Created Successfully");
    }

    @GetMapping
    public List<Service> list(){
        return serviceRepository.findAll();
    }
}

