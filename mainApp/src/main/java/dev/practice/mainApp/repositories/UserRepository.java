package dev.practice.mainApp.repositories;

import dev.practice.mainApp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameOrEmail(String username, String email);

    Optional<User> findByEmail(String email);
}
