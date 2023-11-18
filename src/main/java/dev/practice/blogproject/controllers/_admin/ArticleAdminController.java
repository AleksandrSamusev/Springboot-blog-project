package dev.practice.blogproject.controllers._admin;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.services.ArticleAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/articles")
public class ArticleAdminController {
    private final ArticleAdminService articleService;

    @GetMapping("users/{authorId}")
    public ResponseEntity<List<ArticleFullDto>> getAllArticlesByUserId(
            @RequestHeader("X-Current-User-Id") Long userId,
            @PathVariable("authorId") Long authorId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "ALL", required = false) String status) {
        return new ResponseEntity<>(articleService.getAllArticlesByUserId(userId, authorId, from, size, status),
                HttpStatus.OK);
    }
}