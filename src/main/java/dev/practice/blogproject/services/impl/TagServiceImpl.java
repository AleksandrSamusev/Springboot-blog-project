package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
}
