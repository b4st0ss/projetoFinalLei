package com.example.app.controller;

import com.example.app.dto.auth.UpdateUserDTO;
import com.example.app.dto.auth.UserActivationDTO;
import com.example.app.dto.auth.UserListDTO;
import com.example.app.dto.response.ResponseDTO;
import com.example.app.entity.User;
import com.example.app.entity.status.UserRoles;
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
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserRepository userRepository;

    @PutMapping("/update")
    @PreAuthorize("hasRole('CLIENT') || hasRole('TECH_TEAM') || hasRole('ADMIN')")
    public ResponseEntity updateUser(@RequestBody UpdateUserDTO body,  @AuthenticationPrincipal User logged){
        Optional<User> optionalUser = userRepository.findById(logged.getId());

        if (optionalUser.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        switch (body.info()){
            case "name":
                user.setName(body.newInfo());
                break;
            case "username":
                user.setUsername(body.newInfo());
                break;
            case "password":
                user.setPassword(body.newInfo());
                break;
            case "email":
                user.setEmail(body.newInfo());
                break;
            case "contact":
                user.setContact(body.newInfo());
                break;
            case "address":
                user.setAddress(body.newInfo());
                break;
            default:
                return ResponseEntity.status(404).body(new ResponseDTO("Info doesn't match the existing ones!"));
        }

        try {
            userRepository.save(user);
            return ResponseEntity.ok(new ResponseDTO("Profile updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ResponseDTO("Error saving data to db"));
        }
    }

    @PutMapping("/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity activateUser(@RequestBody UserActivationDTO body){
        Optional<User> optionalUser = userRepository.findById(body.userId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(new ResponseDTO("User not found!"));
        }
        User user = optionalUser.get();

        user.setStatus(body.status());
        try{
            userRepository.save(user);
            String message;

            if (body.status()){
                message = "User Activated Successfully!";
            } else {
                message = "User Deactivated Successfully!";
            }

            return ResponseEntity.ok(new ResponseDTO(message));
        } catch (Exception exception){
            return ResponseEntity.status(500).body(new ResponseDTO("Error updating User"));
        }
    }



    @GetMapping("/all")
    public ResponseEntity getAllUsers(){
        List<User> userList = userRepository.findAll();
        List<UserListDTO> userListDTO = new ArrayList<>();

        for (User user : userList){
            userListDTO.add(new UserListDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().toString(), user.getStatus()));
        }

        return ResponseEntity.ok(userListDTO);
    }


    @GetMapping("/profile")
    public ResponseEntity getUser(@AuthenticationPrincipal User logged){
        Optional<User> optionalUser = userRepository.findById(logged.getId());
        return ResponseEntity.ok(optionalUser.get());
    }


//    @GetMapping
//    public List<User> list() {
//        return repository.findAll();
//    }
}