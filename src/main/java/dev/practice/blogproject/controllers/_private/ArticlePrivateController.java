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

import java.util.List;

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
    public ResponseEntity<Object> getArticleById(@RequestHeader("X-Current-User-Id") Long userId,
                                                 @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.getArticleById(userId, articleId).get(), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<ArticleFullDto>> getAllArticlesByUserId(
            @RequestHeader("X-Current-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String status) {
        return new ResponseEntity<>(articleService.getAllArticlesByUserId(userId, from, size, status), HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<HttpStatus> deleteArticle(@RequestHeader("X-Current-User-Id") Long userId,
                              @PathVariable("articleId") Long articleId) {
        articleService.deleteArticle(userId, articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{articleId}/publish")
    public ResponseEntity<ArticleFullDto> publishArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                        @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.publishArticle(userId, articleId), HttpStatus.OK);
    }
}
