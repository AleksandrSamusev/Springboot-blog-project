package dev.practice.mainApp.article;

import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticlePublicService;
import dev.practice.mainApp.services.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ArticlePublicServiceImplIntTest {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final ArticlePublicService articleService;
    private final TagService tagService;


    private final User user = new User(null, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final User user2 = new User(null, "Admin", "Admin", "ADMIN",
            "admin@gmail.com", LocalDate.of(1990, 9, 10), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, new HashSet<>(), new HashSet<>());
    private final Article article2 = new Article(null, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now().minusDays(2),
            ArticleStatus.PUBLISHED, 0L, new HashSet<>(), new HashSet<>());
    private final Article article3 = new Article(null, "The title",
            "Very interesting information", user, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());

    private final Article articleWithTags1 = new Article(1L, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now().minusDays(2),
            ArticleStatus.PUBLISHED, 0L, new HashSet<>(), new HashSet<>());

    private final Article articleWithTags2 = new Article(2L, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now().minusDays(2),
            ArticleStatus.PUBLISHED, 0L, new HashSet<>(), new HashSet<>());

    @Test
    void article_test_2_Given_anyUser_When_getAllArticles_Then_returnAllPublishedArticlesNewFirst() {
        dropDB();
        userRepository.save(user);
        Article newer = articleRepository.save(article);
        Article older = articleRepository.save(article2);
        Article created = articleRepository.save(article3);

        List<ArticleShortDto> result = articleService.getAllArticles(0, 10);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isInstanceOf(ArticleShortDto.class);
        assertThat(result.get(0).getArticleId()).isEqualTo(newer.getArticleId());
        assertThat(result.get(1).getArticleId()).isEqualTo(older.getArticleId());
    }

    @Test
    void article_test_5_Given_anyUserArticleExist_When_getArticleById_Then_returnArticle() {
        dropDB();
        userRepository.save(user);
        Article saved = articleRepository.save(article);

        ArticleShortDto result = articleService.getArticleById(saved.getArticleId());

        assertThat(result.getArticleId()).isEqualTo(saved.getArticleId());
        assertThat(result).isInstanceOf(ArticleShortDto.class);
    }

    @Test
    void article_test_6_Given_anyUserArticleNotExist_When_getArticleById_Then_throwException() {
        dropDB();

        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> articleService.getArticleById(Long.MAX_VALUE));
        assertEquals(String.format("Article with id %d wasn't found", Long.MAX_VALUE), exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void article_test_7_Given_anyUserArticleNotPublished_When_getArticleById_Then_throwException() {
        dropDB();
        userRepository.save(user);
        Article saved = articleRepository.save(article3);

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.getArticleById(saved.getArticleId()));
        assertEquals(String.format("Article with id %d is not published yet", saved.getArticleId()),
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    void article_test_10_Given_anyUserAuthorExist_When_getAllArticlesByUserId_Then_returnArticles() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        for (int i = 0; i < 5; i++) {
            articleRepository.save(new Article(null, String.valueOf(20 + i), "some information", author,
                    LocalDateTime.now(), LocalDateTime.now(), ArticleStatus.PUBLISHED, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        articleRepository.save(new Article(null, "r", "some information", author,
                LocalDateTime.now(), null, ArticleStatus.MODERATING, 0L, new HashSet<>(),
                new HashSet<>()));

        List<ArticleShortDto> result = articleService.getAllArticlesByUserId(author.getUserId(), 0, 10);

        assertThat(articleRepository.findAll().size()).isEqualTo(26);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0)).isInstanceOf(ArticleShortDto.class);
        assertThat(result.get(0).getTitle()).isEqualTo("24");
    }

    @Test
    public void article_test_40_Given_ValidId_When_GetAllArticlesByTag_Then_ReturnList() {
        dropDB();
        Article article2 = new Article(null, "A pretty cat",
                "Very interesting information", user, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), ArticleStatus.PUBLISHED, 0L,
                new HashSet<>(), new HashSet<>());

        User savedUser = userRepository.save(user);
        Article savedArticle1 = articleRepository.save(article);
        TagFullDto createdTag = tagService.createTag(new TagNewDto("tag1"), savedArticle1.getArticleId());
        Article savedArticle2 = articleRepository.save(article2);
        tagService.addTagsToArticle(savedUser.getUserId(), savedArticle2.getArticleId(),
                List.of(new TagNewDto(createdTag.getName())));

        List<ArticleShortDto> result = articleService.getAllArticlesByTag(createdTag.getTagId());

        Assertions.assertEquals(result.size(), 2);
        Assertions.assertEquals(result.get(0).getArticleId(), savedArticle2.getArticleId());
        Assertions.assertEquals(result.get(1).getArticleId(), savedArticle1.getArticleId());
        Assertions.assertEquals(new ArrayList<>(result.get(0).getTags()).get(0).getTagId(), createdTag.getTagId());
        Assertions.assertEquals(new ArrayList<>(result.get(1).getTags()).get(0).getTagId(), createdTag.getTagId());
    }


    private void dropDB() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }
}
