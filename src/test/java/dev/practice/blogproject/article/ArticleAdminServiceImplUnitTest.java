package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.mappers.UserMapper;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import dev.practice.blogproject.services.impl.ArticleAdminServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ArticleAdminServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticlePrivateService articlePrivateService;

    @InjectMocks
    private ArticleAdminServiceImpl articleService;

    private final User author = new User(0L, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final User admin = new User(1L, "Ron", "Weasley", "RW",
            "rw@gmail.com", LocalDate.of(1981, 9, 16), Role.ADMIN, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final ArticleFullDto fullArticle = new ArticleFullDto(0L, "The empty pot",
            "Very interesting information", UserMapper.toUserShortDto(author), LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 0L, new HashSet<>(), new HashSet<>());

    @Test
    void article_test_1_Given_adminAndExistUser_When_getAllArticlesByUserId_Then_returnAllUserArticles() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        Mockito
                .when(articlePrivateService.getAllArticlesByUserId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(fullArticle));


        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(
                1L, 0L, 0, 10, "ALL");

        assertThat(result.get(0)).isInstanceOf(ArticleFullDto.class);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void article_test_5_Given_notAdminUserExist_When_getAllArticlesByUserId_Then_throwException() {
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> articleService.getAllArticlesByUserId(0L, 0L, 0, 10, "ALL"));
        assertEquals("User with id 0 is not ADMIN. Access is forbidden", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
        Mockito.verify(articlePrivateService, Mockito.times(0))
                .getAllArticlesByUserId(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());
    }
}