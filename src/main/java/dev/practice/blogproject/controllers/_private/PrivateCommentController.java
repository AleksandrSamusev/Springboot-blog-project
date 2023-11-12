package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.services.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/comments/article/{articleId}")
    public ResponseEntity<CommentFullDto> createComment(@PathVariable Long articleId,
                                                        @Valid @RequestBody CommentNewDto dto,
                                                        @RequestHeader("X-Current-User-Id") Long userId) {
        return new ResponseEntity<>(commentService.createComment(articleId, dto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentFullDto> updateComment(@Valid @RequestBody CommentNewDto dto,
                                                        @PathVariable Long commentId,
                                                        @RequestHeader("X-Current-User-Id") Long userId) {
        return new ResponseEntity<>(commentService.updateComment(dto, commentId, userId), HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           @RequestHeader("X-Current-User-Id") Long userId) {
        commentService.deleteComment(commentId, userId);
        return new ResponseEntity<>("Comment successfully deleted", HttpStatus.OK);
    }
}
