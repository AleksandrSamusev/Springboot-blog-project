package dev.practice.blogproject.controllers._public;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.services.ArticlePublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/articles")
public class ArticlePublicController {
    public final ArticlePublicService articleService;

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleShortDto> getArticleById(@PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.getArticleById(articleId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ArticleShortDto>> getAllArticles() {
        return new ResponseEntity<>(articleService.getAllArticles(), HttpStatus.OK);
    }
}
