package dev.practice.mainApp.article;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.ArticlePrivateServiceImpl;
import dev.practice.mainApp.utils.Validations;
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
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ArticlePrivateServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validations validations;

    @InjectMocks
    private ArticlePrivateServiceImpl articleService;

    private final Role roleAdmin = new Role(1L, "ROLE_ADMIN");
    private final Role roleUser = new Role(2L, "ROLE_USER");
    private final User author = new User(0L, "Harry", "Potter", "HP",
            "password", "hp@gmail.com", LocalDate.of(1981, 7, 31),
            Set.of(roleUser), null, false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final User author2 = new User(1L, "Ron", "Weasley", "RW",
            "password", "rw@gmail.com", LocalDate.of(1981, 9, 16),
            Set.of(roleUser), null, false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final Article savedArticle = new Article(0L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, 0L, new HashSet<>(), new HashSet<>());


    @Test
    void articlePr_test_1_Given_validArticleWithoutTagsAndUser_When_createArticle_Then_articleSaved() {
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author);
        Mockito
                .when(articleRepository.save(Mockito.any(Article.class)))
                .thenReturn(savedArticle);

        ArticleFullDto result = articleService.createArticle(author.getUsername(), newArticle);

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
    void articlePr_test_2_Given_bannedUser_When_createArticle_Then_throwException() {
        author.setIsBanned(true);
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.any()))
                .thenReturn(author);
        Mockito
                .doCallRealMethod()
                .when(validations).checkUserIsNotBanned(Mockito.any());

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.createArticle(author.getUsername(), newArticle));
        assertEquals("User with id 0 is blocked", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void articlePr_test_3_Given_notExistingArticle_When_updateArticle_Then_throwException() {
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenThrow(new ResourceNotFoundException("Article with id 1000 wasn't found"));

        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> articleService.updateArticle(author.getUsername(), 1000L, Mockito.any()));
        assertEquals("Article with id 1000 wasn't found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void articlePr_test_4_Given_userIsNotAuthor_When_updateArticle_Then_throwException() {
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author2);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .doCallRealMethod()
                .when(validations).checkUserIsAuthor(Mockito.any(), Mockito.any());

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.updateArticle(author2.getUsername(), 0L, Mockito.any()));
        assertEquals("Article with id 0 is not belongs to user with id 1. Action is forbidden",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void articlePr_test_5_Given_authorisedUserNotAuthor_When_getArticleById_Then_returnedArticleShortDto() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        savedArticle.setPublished(LocalDateTime.now());
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author2);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);

        ArticleShortDto result = (ArticleShortDto) articleService.getArticleById(
                author2.getUsername(), 0L).get();

        assertThat(result).isInstanceOf(ArticleShortDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNotNull();
    }

    @Test
    void articlePr_test_6_Given_authorisedAuthorArticlePublished_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        savedArticle.setPublished(LocalDateTime.now());
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(author.getUsername(), 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
    }

    @Test
    void articlePr_test_7_Given_adminArticlePublished_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        savedArticle.setPublished(LocalDateTime.now());
        author2.setRoles(Set.of(roleAdmin));
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author2);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .when(validations.isAdmin(Mockito.any()))
                .thenReturn(true);

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(author2.getUsername(), 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
    }

    @Test
    void articlePr_test_8_Given_adminArticleRejected_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.REJECTED);
        author2.setRoles(Set.of(roleAdmin));
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author2);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .when(validations.isAdmin(Mockito.any()))
                .thenReturn(true);

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(author2.getUsername(), 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.REJECTED);
    }

    @Test
    void articlePr_test_9_Given_authorArticleModerating_When_getArticleById_Then_returnedArticleFullDto() {
        savedArticle.setStatus(ArticleStatus.MODERATING);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author);

        ArticleFullDto result = (ArticleFullDto) articleService.getArticleById(author.getUsername(), 0L).get();

        assertThat(result).isInstanceOf(ArticleFullDto.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.MODERATING);
    }

    @Test
    void articlePr_test_10_Given_authorizedUserArticleNotPublished_When_getArticleById_Then_throwException() {
        savedArticle.setStatus(ArticleStatus.MODERATING);
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author2);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.getArticleById(author2.getUsername(), 0L));
        assertEquals("Article with id 0 is not published yet",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }


    @Test
    void articlePr_test_11_Given_userNotAuthor_When_deleteArticle_Then_throwException() {
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author2);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .doCallRealMethod()
                .when(validations).checkUserIsAuthor(Mockito.any(), Mockito.any());

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.deleteArticle(author2.getUsername(), 0L));
        assertEquals("Article with id 0 is not belongs to user with id 1. Action is forbidden",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    void articlePr_test_12_Given_articleCreated_When_publishArticle_Then_returnArticle() {
        Mockito
                .when(validations.checkUserExistsByUsernameOrEmail(Mockito.anyString()))
                .thenReturn(author);
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .when(articleRepository.save(Mockito.any()))
                .thenReturn(savedArticle);

        ArticleFullDto result = articleService.publishArticle(author.getUsername(), 0L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.MODERATING);
    }
}