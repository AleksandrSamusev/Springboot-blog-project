package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<CommentShortDto> toCommentDtos(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toList());
    }

}
