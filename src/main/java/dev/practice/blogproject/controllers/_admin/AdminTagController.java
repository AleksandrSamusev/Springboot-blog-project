package dev.practice.blogproject.controllers._admin;

import dev.practice.blogproject.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminTagController {

    private final TagService tagService;

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId,
                                       @RequestHeader("X-Current-User-Id") Long userId) {
        tagService.deleteTag(tagId, userId);
        return new ResponseEntity<>("Tag with id " + tagId + " deleted", HttpStatus.OK);
    }

}
