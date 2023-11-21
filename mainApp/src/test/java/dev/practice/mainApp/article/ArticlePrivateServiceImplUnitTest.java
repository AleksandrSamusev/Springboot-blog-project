package dev.practice.mainApp.article;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.ArticlePrivateServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ArticlePrivateServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArticlePrivateServiceImpl articleService;


    private final User author = new User(0L, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final User author2 = new User(1L, "Ron", "Weasley", "RW",
            "rw@gmail.com", LocalDate.of(1981, 9, 16), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final Article savedArticle = new Article(0L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final Article savedArticle2 = new Article(1L, "A pretty cat",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final ArticleUpdateDto update = new ArticleUpdateDto();
    private final Tag tag1 = new Tag(0L, "Potions", new HashSet<>());


    @Test
    void article_test_1_Given_validArticleWithoutTagsAndUser_When_createArticle_Then_articleSaved() {
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));
        Mockito
                .when(articleRepository.save(Mockito.any(Article.class)))
                .thenReturn(savedArticle);

        ArticleFullDto result = articleService.createArticle(0L, newArticle);

        assertThat(result).isNotNull();
        assertThat(result.getTags()).isEqualTo(new HashSet<>());
        assertThat(result.getTitle()).isEqualTo(savedArticle.getTitle());
        assertThat(result.getContent()).isEqualTo(savedArticle.getContent());
        assertThat(result.getAuthor().getUserId()).isEqualTo(0L);
        assertThat(result.getAuthor().getUsername()).isEqualTo("HP");
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.CREATED);
        Mockito.verify(articleRepository, Mockito.times(1)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_6_Given_bannedUser_When_createArticle_Then_throwException() {
        author.setIsBanned(true);
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.createArticle(0L, newArticle));
        assertEquals("User with id 0 is blocked", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_7_Given_NotExistingUser_When_createArticle_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> articleService.createArticle(1L, newArticle));
        assertEquals("User with id 1 wasn't found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_12_Given_notExistingArticle_When_updateArticle_Then_throwException() {
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));

        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> articleService.updateArticle(0L, 1000L, Mockito.any()));
        assertEquals("Article with id 1000 wasn't found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_13_Given_userIsNotAuthor_When_updateArticle_Then_throwException() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(author2));
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.updateArticle(1L, 0L, Mockito.any()));
        assertEquals("Article with id 0 is not belongs to user with id 1. Action is forbidden",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_16_Given_authorisedUserNotAuthor_When_getArticleById_Then_returnedArticleShortDto() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        savedArticle.setPublished(LocalDateTime.now());
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(author2));

        ArticleShortDto result = (ArticleShortDto) articleService.getArticleById(1L, 0L).get();

        assertThat(result).isInstanceOf(ArticleShortDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNotNull();
    }

    @Test
    void article_test_17_Given_authorisedAuthorArticlePublished_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        savedArticle.setPublished(LocalDateTime.now());
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(0L, 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
    }

    @Test
    void article_test_18_Given_adminArticlePublished_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        savedArticle.setPublished(LocalDateTime.now());
        author2.setRole(Role.ADMIN);
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(author2));

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(1L, 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
    }

    @Test
    void article_test_19_Given_adminArticleRejected_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.REJECTED);
        author2.setRole(Role.ADMIN);
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(author2));

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(1L, 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.REJECTED);
    }

    @Test
    void article_test_20_Given_authorArticleModerating_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.MODERATING);
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(0L, 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.MODERATING);
    }

    @Test
    void article_test_21_Given_authorizedUserArticleNotPublished_When_getArticleById_Then_throwException() {
        savedArticle.setStatus(ArticleStatus.MODERATING);
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(author2));

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.getArticleById(1L, 0L));
        assertEquals("Article with id 0 is not published yet",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    void article_test_26_Given_userNotAuthor_When_deleteArticle_Then_throwException() {
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(author2));

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.deleteArticle(1L, 0L));
        assertEquals("Article with id 0 is not belongs to user with id 1. Action is forbidden",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    void article_test_37_Given_articleCreated_When_publishArticle_Then_returnArticle() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(author));
        Mockito
                .when(articleRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(savedArticle));
        Mockito
                .when(articleRepository.save(Mockito.any()))
                .thenReturn(savedArticle);

        ArticleFullDto result = articleService.publishArticle(0L, 0L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.MODERATING);
    }


}
