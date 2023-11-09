package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.CommentDto;
import dev.practice.blogproject.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment toComment(CommentDto dto) {
        return new Comment(
                dto.getCommentId(),
                dto.getComment(),
                dto.getCreated(),
                dto.getArticle(),
                dto.getUser()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getCommentId(),
                comment.getComment(),
                comment.getCreated(),
                comment.getArticle(),
                comment.getUser()
        );
    }

    public static List<Comment> toComments(List<CommentDto> dtos) {
        return dtos.stream().map(CommentMapper::toComment).collect(Collectors.toList());
    }

    public static List<CommentDto> toCommentDtos(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

}
