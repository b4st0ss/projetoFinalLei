package com.example.app;

import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(UserRepository userRepository){
//        return  args -> {
//            User admin = new User(
//                    "diogo",
//                    "drbbadmin124",
//                    "diogorbbastos@gmail.com",
//                    "admin",
//                    true
//            );
//            userRepository.save(admin);
//        };
//    }


    // roles: admin, dev, client

}
