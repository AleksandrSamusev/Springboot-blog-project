package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public CommentFullDto createComment(Long articleId, CommentNewDto dto, Long userId) {
        isUserExists(userId);
        isArticleExists(articleId);
        User user = userRepository.findById(userId).get();
        Article article = articleRepository.findById(articleId).get();
        Comment comment = CommentMapper.toComment(dto, user, article);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment with ID = " + savedComment.getCommentId() + " saved");
        return CommentMapper.toCommentFullDto(savedComment);
    }

    @Override
    public CommentFullDto updateComment(CommentNewDto dto, Long commentId, Long userId) {
        isCommentExists(commentId);
        isUserExists(userId);
        Comment comment = commentRepository.findById(commentId).get();
        isActionAllowed(userId, comment);
        comment.setComment(dto.getComment());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with ID = " + updatedComment.getCommentId() + " updated");
        return CommentMapper.toCommentFullDto(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        isCommentExists(commentId);
        isUserExists(userId);
        Comment comment = commentRepository.findById(commentId).get();
        User user = userRepository.findById(userId).get();
        if (userId.equals(comment.getCommentAuthor().getUserId())
                || user.getRole().name().equals("ADMIN")) {
            commentRepository.deleteById(commentId);
        } else {
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }

    @Override
    public CommentFullDto getCommentById(Long commentId) {
        isCommentExists(commentId);
        return CommentMapper.toCommentFullDto(commentRepository.findById(commentId).get());
    }

    @Override
    public List<CommentFullDto> getAllCommentsToArticle(Long articleId) {
        isArticleExists(articleId);
        return articleRepository.findById(articleId).get().getComments()
                .stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }

    private void isUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with given Id = " + userId + " not found");
        }
    }

    private void isArticleExists(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article with given Id = " + articleId + " not found");
        }
    }

    private void isCommentExists(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment with given Id = " + commentId + " not found");
        }
    }

    private void isActionAllowed(Long userId, Comment comment) {
        if (!userId.equals(comment.getCommentAuthor().getUserId())) {
            throw new ActionForbiddenException("Action forbidden for given user");
        }
    }

}
