package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.CommentMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Comment;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.CommentRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public CommentFullDto createComment(Long articleId, CommentNewDto dto, Long userId) {
        if (userId == null) {
            throw new ActionForbiddenException("Action forbidden for given user");
        }
        if (articleId == null || dto == null) {
            throw new InvalidParameterException("Invalid parameter");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User with given ID " + userId + " not found"));
        Article article = articleRepository.findById(articleId).orElseThrow(() ->
                new ResourceNotFoundException("Article with given ID " + articleId + " not found"));

        Comment comment = new Comment();
        comment.setComment(dto.getComment());
        comment.setCommentAuthor(user);
        comment.setArticle(article);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment with ID = " + savedComment.getCommentId() + " saved");
        return CommentMapper.toCommentFullDto(savedComment);
    }

    @Override
    public CommentFullDto updateComment(CommentNewDto dto, Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment with given ID = " + commentId + " not found"));
        if (!userId.equals(comment.getCommentAuthor().getUserId())) {
            throw new ActionForbiddenException("Action forbidden for given user");
        }
        if (dto == null) {
            throw new InvalidParameterException("Invalid parameter");
        }
        if (dto.getComment() != null) {
            comment.setComment(dto.getComment());
        }
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with ID = " + updatedComment.getCommentId() + " updated");
        return CommentMapper.toCommentFullDto(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            throw new InvalidParameterException("Invalid parameter");
        }
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment with given ID = " + commentId + " not found"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User with given ID = " + userId + " not found"));
        if (userId.equals(comment.getCommentAuthor().getUserId()) || user.getRole().name().equals("ADMIN")) {
            commentRepository.deleteById(commentId);
        } else {
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }
}
