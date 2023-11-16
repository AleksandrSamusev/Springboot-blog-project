package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.services.TagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class PrivateTagController {

    private final TagService tagService;

    @PostMapping("/tags/articles/{articleId}")
    public ResponseEntity<TagFullDto> createTag(@Valid @RequestBody TagNewDto dto,
                                                @PathVariable Long articleId,
                                                @RequestHeader("X-Current-User-Id") Long userId) {
        return new ResponseEntity<>(tagService.createTag(dto, articleId), HttpStatus.CREATED);
    }


}
