package dev.practice.mainApp.controllers._admin;

import dev.practice.mainApp.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/admin")
public class TagAdminController {

    private final TagService tagService;

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<?> deleteTag(@AuthenticationPrincipal UserDetails userDetails,
                                       @PathVariable Long tagId) {
        tagService.deleteTag(tagId, userDetails.getUsername());
        return new ResponseEntity<>("Tag with id " + tagId + " deleted", HttpStatus.OK);
    }

}
