package com.example.app.controller;

import com.example.app.dto.LoginRequestDTO;
import com.example.app.dto.RegisterRequestDTO;
import com.example.app.dto.ResponseDTO;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body){
        Optional<User> userOptional = userRepository.findByUsername(body.username());

        if(userOptional.isEmpty()){
            return ResponseEntity.status(404).body("User not found");
        }

        User user = userOptional.get();

        if(passwordEncoder.matches(body.password(), user.getPassword())){
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }

        return ResponseEntity.status(401).body("Invalid password");
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        // Optional<User> user = this.userRepository.findByUsername(body.username());
        if (userRepository.findByUsername(body.username()).isPresent() ||
                userRepository.findByEmail(body.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Username or email already exists");
        }

        User newUser = new User();
        newUser.setUsername(body.username());
        newUser.setEmail(body.email());
        newUser.setName(body.name());
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setRole("CLIENT");
        newUser.setStatus(false);
        userRepository.save(newUser);

        return ResponseEntity.ok("Registo feito com sucesso!");
    }

}
