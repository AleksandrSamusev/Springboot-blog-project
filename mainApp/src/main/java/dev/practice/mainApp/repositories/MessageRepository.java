package dev.practice.mainApp.repositories;

import dev.practice.mainApp.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
