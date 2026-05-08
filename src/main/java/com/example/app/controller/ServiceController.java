package com.example.app.controller;

import com.example.app.dto.service.Dev_ServiceDTO;
import com.example.app.dto.service.ServiceRequestDTO;
import com.example.app.dto.service.ServiceStatusDTO;
import com.example.app.entity.Dev_Service;
import com.example.app.entity.Service;
import com.example.app.entity.User;
import com.example.app.repository.Dev_ServiceRepository;
import com.example.app.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final Dev_ServiceRepository devServiceRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody ServiceRequestDTO body, @AuthenticationPrincipal User logged){

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


    @PostMapping("/associate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity addDevtoService(@RequestBody Dev_ServiceDTO body, @AuthenticationPrincipal User logged){
        if (devServiceRepository.findByDevAndService(body.id_dev(), body.id_service()).isPresent()) {
            return ResponseEntity.badRequest().body("The developer has already been chosen");
        } else if(serviceRepository.findServiceByStatusAndId("ACCEPTED", body.id_service()).isEmpty()){
           return ResponseEntity.badRequest().body("Service status was not accepted to proceed");
        }

        Dev_Service newDevService = new Dev_Service();
        newDevService.setService_id(body.id_service());
        newDevService.setDev_id(body.id_dev());
        newDevService.setAdmin_id(logged.getId());

        devServiceRepository.save(newDevService);

        return ResponseEntity.ok("Dev was successfully associated to service " + body.id_service());
    }


    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity updateServiceStatus(@RequestBody ServiceStatusDTO body) {

        Optional<Service> optionalService = serviceRepository.findById(body.id_service());

        if (optionalService.isEmpty()) {
            return ResponseEntity.status(404).body("Service not found");
        }

        Service service = optionalService.get();

        if (body.status() == 1) {
            service.setStatus("ACCEPTED");
        } else if (body.status() == 0) {
            service.setStatus("REJECTED");
        } else {
            return ResponseEntity.badRequest().body("Invalid Action");
        }

        serviceRepository.save(service);

        return ResponseEntity.ok("Service status was changed to " + service.getStatus());
    }





    @GetMapping
    public List<Service> list(){
        return serviceRepository.findAll();
    }
}

