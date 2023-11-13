package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.impl.ArticlePrivateServiceImpl;
import dev.practice.blogproject.services.impl.TagServiceImpl;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ArticlePrivateServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ArticlePrivateServiceImpl articleService;

    @InjectMocks
    private TagServiceImpl tagService;

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
        Mockito
                .when(articleRepository.getReferenceById(0L))
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
        assertThrows(ActionForbiddenException.class, () -> articleService.createArticle(
                author.getUserId(), newArticle), "Incorrect exception");
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_7_Given_NotExistingUser_When_createArticle_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> articleService.createArticle(1L, newArticle));
        assertEquals("User with id 1 wasn't found", exception.getMessage(),
                "Incorrect message");
        assertThrows(ResourceNotFoundException.class, () -> articleService.createArticle(
                author.getUserId(), newArticle), "Incorrect exception");
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
        assertThrows(ResourceNotFoundException.class, () -> articleService.updateArticle(
                0L, 1000L, Mockito.any()), "Incorrect exception");
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
        assertThrows(ActionForbiddenException.class, () -> articleService.updateArticle(
                1L, 0L, Mockito.any()), "Incorrect exception");
        Mockito.verify(articleRepository, Mockito.times(0)).save(Mockito.any(Article.class));
    }


}
