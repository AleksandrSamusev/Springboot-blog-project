package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);
}
