package dev.practice.blogproject.repositories;

import dev.practice.blogproject.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
   Tag findTagByName(String name);
}
