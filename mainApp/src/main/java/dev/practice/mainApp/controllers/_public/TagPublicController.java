package dev.practice.mainApp.controllers._public;


import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/public")
public class TagPublicController {

    private final TagService tagService;

    @GetMapping("/tags/articles/{articleId}")
    public ResponseEntity<List<TagFullDto>> getAllArticleTags(@PathVariable Long articleId) {
        return new ResponseEntity<>(tagService.getAllArticleTags(articleId), HttpStatus.OK);
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<TagFullDto> getTagById(@PathVariable Long tagId) {
        return new ResponseEntity<>(tagService.getTagById(tagId), HttpStatus.OK);
    }
}
