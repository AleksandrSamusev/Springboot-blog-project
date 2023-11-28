package dev.practice.mainApp.controllers._public;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.services.ArticlePublicService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ArticleShortDto> getArticleById(@PathVariable("articleId") Long articleId,
                                                          HttpServletRequest request) throws JsonProcessingException {
        return new ResponseEntity<>(articleService.getArticleById(articleId, request), HttpStatus.OK);
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

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<List<ArticleShortDto>> getAllArticlesByTag(@PathVariable Long tagId) {
        return new ResponseEntity<>(articleService.getAllArticlesByTag(tagId), HttpStatus.OK);
    }
}
