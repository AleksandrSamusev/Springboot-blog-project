package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.services.ArticlePrivateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/articles")
public class ArticlePrivateController {
    private final ArticlePrivateService articleService;

    @PostMapping
    public ResponseEntity<ArticleFullDto> createArticle(@RequestHeader("X-Current-User-Id") long userId,
                                        @RequestBody ArticleNewDto newArticle) {
        return new ResponseEntity<>(articleService.createArticle(userId, newArticle), HttpStatus.CREATED);
    }

    @PatchMapping("/{articleId}")
    public ResponseEntity<ArticleFullDto> updateArticle(@RequestHeader("X-Current-User-Id") long userId,
                                        @PathVariable("articleId") long articleId,
                                        @RequestBody ArticleUpdateDto updateArticle) {
        return new ResponseEntity<>(articleService.updateArticle(userId, articleId, updateArticle), HttpStatus.OK);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<?> getArticleById(@RequestHeader("X-Current-User-Id") long userId,
                                         @PathVariable("articleId") long articleId) {
        return new ResponseEntity<>(articleService.getArticleById(userId, articleId), HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}")
    public void deleteArticle(@RequestHeader("X-Current-User-Id") long userId,
                              @PathVariable("articleId") long articleId) {
        articleService.deleteArticle(userId, articleId);
    }
}