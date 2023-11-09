package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.services.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
}
