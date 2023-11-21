package dev.practice.mainApp.repositories;

import dev.practice.mainApp.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByName(String name);
}
