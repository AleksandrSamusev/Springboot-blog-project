package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.comment.CommentFullDto;
import dev.practice.mainApp.dtos.comment.CommentNewDto;

import java.util.List;

public interface CommentService {
    CommentFullDto createComment(Long articleId, CommentNewDto dto, String login);

    CommentFullDto updateComment(CommentNewDto dto, Long commentId, String login);

    void deleteComment(Long commentId, String login);

    CommentFullDto getCommentById(Long commentId);

    List<CommentFullDto> getAllCommentsToArticle(Long articleId);
}
