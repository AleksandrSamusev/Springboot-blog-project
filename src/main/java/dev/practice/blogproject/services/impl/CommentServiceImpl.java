package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.repositories.CommentRepository;
import dev.practice.blogproject.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
}
