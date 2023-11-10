package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.CommentFullDto;
import dev.practice.blogproject.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment toComment(CommentFullDto dto) {
        return new Comment(
                dto.getCommentId(),
                dto.getComment(),
                dto.getCreated(),
                dto.getArticle(),
                dto.getUser()
        );
    }

    public static CommentFullDto toCommentDto(Comment comment) {
        return new CommentFullDto(
                comment.getCommentId(),
                comment.getComment(),
                comment.getCreated(),
                comment.getArticle(),
                comment.getUser()
        );
    }

    public static List<Comment> toComments(List<CommentFullDto> dtos) {
        return dtos.stream().map(CommentMapper::toComment).collect(Collectors.toList());
    }

    public static List<CommentFullDto> toCommentDtos(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

}
