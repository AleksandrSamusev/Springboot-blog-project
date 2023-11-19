package dev.practice.blogproject.controllers._public;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.services.ArticlePublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<ArticleShortDto>> getAllArticles(@RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(articleService.getAllArticles(from, size), HttpStatus.OK);
    }

    @GetMapping("users/{authorId}")
    public ResponseEntity<List<ArticleShortDto>> getAllArticlesByUserId(
            @PathVariable("authorId") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(articleService.getAllArticlesByUserId(userId, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{articleId}/like")
    public ResponseEntity<ArticleShortDto> likeArticle(@PathVariable Long articleId) {
        return new ResponseEntity<>(articleService.likeArticle(articleId), HttpStatus.OK);
    }
}
