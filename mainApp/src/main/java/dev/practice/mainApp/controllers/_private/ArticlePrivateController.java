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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/articles")
public class ArticlePrivateController {
    private final ArticlePrivateService articleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ArticleFullDto> createArticle(@AuthenticationPrincipal UserDetails userDetails,
                                                        @Valid @RequestBody ArticleNewDto newArticle) {

        return new ResponseEntity<>(articleService.createArticle(userDetails.getUsername(), newArticle),
                HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/{articleId}")
    public ResponseEntity<ArticleFullDto> updateArticle(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable("articleId") Long articleId,
                                                        @RequestBody ArticleUpdateDto updateArticle) {
        return new ResponseEntity<>(articleService.updateArticle(userDetails.getUsername(), articleId, updateArticle),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{articleId}")
    public ResponseEntity<Object> getArticleById(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.getArticleById(userDetails.getUsername(), articleId).get(),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping()
    public ResponseEntity<List<ArticleFullDto>> getAllArticlesByUserId(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String status) {
        return new ResponseEntity<>(articleService.getAllArticlesByUserId(
                userDetails.getUsername(), from, size, status), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{articleId}")
    public ResponseEntity<HttpStatus> deleteArticle(@AuthenticationPrincipal UserDetails userDetails,
                                                    @PathVariable("articleId") Long articleId) {
        articleService.deleteArticle(userDetails.getUsername(), articleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PatchMapping("/{articleId}/publish")
    public ResponseEntity<ArticleFullDto> publishArticle(@AuthenticationPrincipal UserDetails userDetails,
                                                         @PathVariable("articleId") Long articleId) {
        return new ResponseEntity<>(articleService.publishArticle(userDetails.getUsername(), articleId), HttpStatus.OK);
    }
}
