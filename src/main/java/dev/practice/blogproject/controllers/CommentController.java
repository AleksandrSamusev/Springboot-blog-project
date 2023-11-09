package dev.practice.blogproject.controllers;

import dev.practice.blogproject.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;
}
