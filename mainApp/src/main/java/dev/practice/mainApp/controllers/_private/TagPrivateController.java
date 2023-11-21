package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.services.TagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private/tags")
public class TagPrivateController {
    private final TagService tagService;

    @PostMapping("/articles/{articleId}")
    public ResponseEntity<TagFullDto> createTag(@Valid @RequestBody TagNewDto dto,
                                                @PathVariable Long articleId,
                                                @RequestHeader("X-Current-User-Id") Long userId) {
        return new ResponseEntity<>(tagService.createTag(dto, articleId), HttpStatus.CREATED);
    }

    @PatchMapping("/articles/{articleId}/add")
    public ResponseEntity<ArticleFullDto> addTagsToArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                           @PathVariable Long articleId,
                                                           @RequestParam List<TagNewDto> tags) {
        return new ResponseEntity<>(tagService.addTagsToArticle(userId, articleId, tags), HttpStatus.OK);
    }

    @PatchMapping("/articles/{articleId}/remove")
    public ResponseEntity<ArticleFullDto> removeTagsFromArticle(@RequestHeader("X-Current-User-Id") Long userId,
                                                                @PathVariable Long articleId,
                                                                @RequestParam List<Long> tags) {
        return new ResponseEntity<>(tagService.removeTagsFromArticle(userId, articleId, tags), HttpStatus.OK);
    }


}
