package dev.practice.mainApp.mappers;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleMapper {

    public static Article toArticleFromNew(ArticleNewDto dto, User user) {
        Article article = new Article();
        article.setTitle(dto.getTitle().trim());
        article.setContent(dto.getContent());
        article.setAuthor(user);
        article.setLikes(0L);
        article.setViews(0L);
        article.setComments(new HashSet<>());
        article.setTags(new HashSet<>());
        return article;
    }

    public static ArticleFullDto toArticleFullDto(Article article) {
        return new ArticleFullDto(
                article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                UserMapper.toUserShortDto(article.getAuthor()),
                article.getCreated(),
                article.getPublished(),
                article.getStatus(),
                article.getLikes(),
                article.getViews(),
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
                article.getViews(),
                article.getComments().stream().map(CommentMapper::toCommentShortDto).collect(Collectors.toSet()),
                article.getTags().stream().map(TagMapper::toTagShortDto).collect(Collectors.toSet()));
    }

    public static Article toArticleFromUpdate(Article article, ArticleUpdateDto updateArticle) {
        article.setTitle(updateArticle.getTitle() == null ? article.getTitle() : updateArticle.getTitle().trim());
        article.setContent(updateArticle.getContent() == null ? article.getContent() : updateArticle.getContent());
        article.setLikes(0L);
        article.setComments(new HashSet<>());
        article.setStatus(ArticleStatus.CREATED);
        article.setPublished(null);
        return article;
    }

    public static List<ArticleShortDto> toListArticleShort(List<Article> articles) {
        return articles.stream().map(ArticleMapper::toArticleShortDto).toList();
    }

    public static List<ArticleFullDto> toListArticleFull(List<Article> articles) {
        return articles.stream().map(ArticleMapper::toArticleFullDto).toList();
    }

}
