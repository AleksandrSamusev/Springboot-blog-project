package dev.practice.mainApp; //

import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.mappers.UserMapper;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
@AllArgsConstructor
public class BlogMain implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(BlogMain.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Role role = new Role(null, "ADMIN");
        Role role2 = new Role(null, "USER");

        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(role);
            roleRepository.save(role2);
        }
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
