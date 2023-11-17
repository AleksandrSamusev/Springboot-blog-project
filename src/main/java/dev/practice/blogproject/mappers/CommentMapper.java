package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Comment;
import dev.practice.blogproject.models.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return new CommentFullDto(
                comment.getCommentId(),
                comment.getComment(),
                comment.getCreated(),
                comment.getArticle().getArticleId(),
                UserMapper.toUserShortDto(comment.getCommentAuthor())
        );
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return new CommentShortDto(comment.getCommentId(),
                comment.getComment(),
                comment.getCreated(),
                comment.getArticle().getArticleId(),
                comment.getCommentAuthor().getUserId());
    }

    public static Comment toComment(CommentNewDto dto, User user, Article article) {
        Comment comment = new Comment();
        comment.setComment(dto.getComment());
        comment.setCommentAuthor(user);
        comment.setArticle(article);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

}
