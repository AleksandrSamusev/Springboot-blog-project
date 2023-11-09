package dev.practice.blogproject.controllers;

import dev.practice.blogproject.services.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
}
