package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsernameOrEmail(String username, String email);
}
