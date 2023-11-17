package dev.practice.blogproject.controllers._public;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.services.CommentService;
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
@RequestMapping("/api/v1/public/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentFullDto> getCommentById(@PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
    }

    @GetMapping("/articles/{articleId}")
    public ResponseEntity<List<CommentFullDto>> getAllCommentsToArticle(@PathVariable Long articleId) {
        return new ResponseEntity<>(commentService.getAllCommentsToArticle(articleId), HttpStatus.OK);
    }

}
