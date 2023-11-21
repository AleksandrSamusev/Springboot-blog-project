package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
