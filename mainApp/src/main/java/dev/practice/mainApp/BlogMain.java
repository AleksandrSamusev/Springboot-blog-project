package dev.practice.mainApp;

import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.utils.Validations;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@AllArgsConstructor
public class BlogMain implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final Validations validations;

    public static void main(String[] args) {
        SpringApplication.run(BlogMain.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Role role = new Role(null, "ROLE_ADMIN");
        Role role2 = new Role(null, "ROLE_USER");
        if (!validations.isRoleExistsByName("ROLE_ADMIN")) {
            roleRepository.save(role);
        }
        if (!validations.isRoleExistsByName("ROLE_USER")) {
            roleRepository.save(role2);
        }
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
