package dev.practice.mainApp.repositories;

import dev.practice.mainApp.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
