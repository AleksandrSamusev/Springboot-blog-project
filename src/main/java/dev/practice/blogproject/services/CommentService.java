package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;

import java.util.List;

public interface CommentService {
    CommentFullDto createComment(Long articleId, CommentNewDto dto, Long userId);

    CommentFullDto updateComment(CommentNewDto dto, Long commentId, Long userId);

    void deleteComment(Long commentId, Long userId);

    CommentFullDto getCommentById(Long commentId);

    List<CommentFullDto> getAllCommentsToArticle(Long articleId);
}
