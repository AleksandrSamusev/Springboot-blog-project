package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.models.Article;

import java.util.List;
import java.util.stream.Collectors;

public class ArticleMapper {

    public static ArticleFullDto toArticleDto(Article article) {
        return new ArticleFullDto(
                article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                UserMapper.toUserShortDto(article.getAuthor()),
                article.getCreated(),
                article.getPublished(),
                article.getStatus(),
                article.getLikes(),
                article.getComments().stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toSet()),
                article.getTags().stream().map(TagMapper::toTagShortDto).collect(Collectors.toSet())
        );
    }

    public static ArticleShortDto toArticleShortDto(Article article) {
        return new ArticleShortDto(article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                UserMapper.toUserShortDto(article.getAuthor()),
                article.getPublished(),
                article.getLikes(),
                article.getComments().stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toSet()),
                article.getTags().stream().map(TagMapper::toTagShortDto).collect(Collectors.toSet()));
    }

    public static List<ArticleFullDto> toArticleDtos(List<Article> articles) {
        return articles.stream().map(ArticleMapper::toArticleDto).collect(Collectors.toList());
    }

}
