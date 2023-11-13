package dev.practice.blogproject.mappers;
import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.User;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ArticleMapper {

    public static Article toArticleFromNew(ArticleNewDto dto, User user) {
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setAuthor(user);
        article.setLikes(0L);
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

}
