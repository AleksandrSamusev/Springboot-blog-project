package dev.practice.mainApp.controllers._admin;

import dev.practice.mainApp.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/admin")
public class TagAdminController {

    private final TagService tagService;

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable Long tagId,
                                       @RequestHeader("X-Current-User-Id") Long userId) {
        tagService.deleteTag(tagId, userId);
        return new ResponseEntity<>("Tag with id " + tagId + " deleted", HttpStatus.OK);
    }

}
