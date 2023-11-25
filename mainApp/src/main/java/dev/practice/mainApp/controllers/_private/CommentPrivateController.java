package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.comment.CommentFullDto;
import dev.practice.mainApp.dtos.comment.CommentNewDto;
import dev.practice.mainApp.services.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class CommentPrivateController {
    private final CommentService commentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/comments/article/{articleId}")
    public ResponseEntity<CommentFullDto> createComment(@AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable Long articleId,
                                                        @Valid @RequestBody CommentNewDto dto) {
        return new ResponseEntity<>(commentService.createComment(
                articleId, dto, userDetails.getUsername()), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentFullDto> updateComment(@AuthenticationPrincipal UserDetails userDetails,
                                                        @Valid @RequestBody CommentNewDto dto,
                                                        @PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.updateComment(
                dto, commentId, userDetails.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long commentId) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return new ResponseEntity<>("Comment successfully deleted", HttpStatus.OK);
    }
}
