package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.models.Article;

import java.util.List;
import java.util.stream.Collectors;

public class ArticleMapper {

    public static Article toArticle(ArticleFullDto dto) {
        return new Article(
                dto.getArticleId(),
                dto.getTitle(),
                dto.getContent(),
                dto.getAuthor(),
                dto.getCreated(),
                dto.getPublished(),
                dto.getStatus(),
                dto.getLikes(),
                dto.getComments(),
                dto.getTags()
        );
    }

    public static ArticleFullDto toArticleDto(Article article) {
        return new ArticleFullDto(
                article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                article.getAuthor(),
                article.getCreated(),
                article.getPublished(),
                article.getStatus(),
                article.getLikes(),
                article.getComments(),
                article.getTags()
        );
    }

    public static List<Article> toArticles(List<ArticleFullDto> dtos) {
        return dtos.stream().map(ArticleMapper::toArticle).collect(Collectors.toList());
    }

    public static List<ArticleFullDto> toArticleDtos(List<Article> articles) {
        return articles.stream().map(ArticleMapper::toArticleDto).collect(Collectors.toList());
    }

}
