package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.services.ArticlePrivateService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ArticleFullDto> createArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                        @Valid @RequestBody ArticleNewDto newArticle) {
        return new ResponseEntity<>(articleService.createArticle(userId, newArticle), HttpStatus.CREATED);
    }

    @PatchMapping("/{articleId}")
    public ResponseEntity<ArticleFullDto> updateArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                        @PathVariable("articleId") Long articleId,
                                                        @RequestBody ArticleUpdateDto updateArticle) {
        return new ResponseEntity<>(articleService.updateArticle(userId, articleId, updateArticle), HttpStatus.OK);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<?> getArticleById(@RequestHeader(value = "X-Current-User-Id") Long userId,
                                            @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.getArticleById(userId, articleId), HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}")
    public void deleteArticle(@RequestHeader("X-Current-User-Id") Long userId,
                              @PathVariable("articleId") Long articleId) {
        articleService.deleteArticle(userId, articleId);
    }
}
