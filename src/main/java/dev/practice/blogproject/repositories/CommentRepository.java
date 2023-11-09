package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
