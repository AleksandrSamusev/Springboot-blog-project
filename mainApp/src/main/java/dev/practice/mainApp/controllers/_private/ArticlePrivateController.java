package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.services.ArticlePrivateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/articles")
public class ArticlePrivateController {
    private final ArticlePrivateService articleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ArticleFullDto> createArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                        @Valid @RequestBody ArticleNewDto newArticle) {
        return new ResponseEntity<>(articleService.createArticle(userId, newArticle), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/{articleId}")
    public ResponseEntity<ArticleFullDto> updateArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                        @PathVariable("articleId") Long articleId,
                                                        @RequestBody ArticleUpdateDto updateArticle) {
        return new ResponseEntity<>(articleService.updateArticle(userId, articleId, updateArticle), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{articleId}")
    public ResponseEntity<Object> getArticleById(@RequestHeader("X-Current-User-Id") Long userId,
                                                 @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.getArticleById(userId, articleId).get(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping()
    public ResponseEntity<List<ArticleFullDto>> getAllArticlesByUserId(
            @RequestHeader("X-Current-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String status) {
        return new ResponseEntity<>(articleService.getAllArticlesByUserId(userId, from, size, status), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<HttpStatus> deleteArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                    @PathVariable("articleId") Long articleId) {
        articleService.deleteArticle(userId, articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/{articleId}/publish")
    public ResponseEntity<ArticleFullDto> publishArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                         @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.publishArticle(userId, articleId), HttpStatus.OK);
    }
}
