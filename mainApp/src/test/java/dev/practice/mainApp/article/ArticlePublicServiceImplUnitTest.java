package dev.practice.mainApp.article;

import dev.practice.mainApp.client.StatsClient;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.services.impl.ArticlePublicServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ArticlePublicServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private StatsClient statsClient; // DO NOT DELETE!!!

    @Mock
    private Validations validations;

    @InjectMocks
    private ArticlePublicServiceImpl articleService;

    private final Role roleUser = new Role(2L, "ROLE_USER");
    private final User author = new User(0L, "Harry", "Potter", "author",
            "password", "hp@gmail.com", LocalDate.of(1981, 7, 31),
            Set.of(roleUser), null, false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final Article savedArticle = new Article(0L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 0L, 0L, new HashSet<>(), new HashSet<>());
    private final Article afterLike = new Article(0L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1L, 0L, new HashSet<>(), new HashSet<>());
    private final Article savedArticle2 = new Article(1L, "A pretty cat",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now().minusDays(2),
            ArticleStatus.PUBLISHED, 0L, 0L, new HashSet<>(), new HashSet<>());


    @Test
    void articlePu_test_1_Given_anyUser_When_getAllArticles_Then_returnAllPublishedArticles() {
        Mockito
                .when(articleRepository.saveAll(Mockito.any()))
                .thenReturn(List.of(savedArticle2, savedArticle));

        List<ArticleShortDto> result = articleService.getAllArticles(0, 10);

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void articlePu_test_2_Given_anyUserArticleExist_When_getArticleById_Then_returnArticle() {

        Mockito
                .when(validations.checkArticleExist(Mockito.any()))
                .thenReturn(savedArticle);
        Mockito
                .when(articleRepository.save(Mockito.any()))
                .thenReturn(savedArticle);

        ArticleShortDto result = articleService.getArticleById(savedArticle.getArticleId(),
                new MockHttpServletRequest());

        assertThat(result.getArticleId()).isEqualTo(0);
    }

    @Test
    void articlePu_test_3_Given_ExistingArticle_When_likeArticle_Then_ArticleLikesIncreaseByOne() {
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .when(articleRepository.save(Mockito.any()))
                .thenReturn(afterLike);

        ArticleShortDto result = articleService.likeArticle(0L);

        assertThat(result.getArticleId()).isEqualTo(afterLike.getArticleId());
        assertThat(result.getLikes()).isEqualTo(1);
    }

    @Test
    void articlePu_test_4_Given_ArticleNotExists_When_likeArticle_Then_ArticleLikesIncreaseByOne() {
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenThrow(new ResourceNotFoundException("Article with id 0 wasn't found"));

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () ->
                articleService.likeArticle(0L));
        Assertions.assertEquals("Article with id 0 wasn't found", ex.getMessage());
    }

    @Test
    void articlePu_test_5_Given_ArticleNotPublished_When_likeArticle_Then_ActionForbidden() {
        Mockito
                .when(validations.checkArticleExist(Mockito.anyLong()))
                .thenReturn(savedArticle);
        Mockito
                .doThrow(new ActionForbiddenException("Article with id %d is not published yet"))
                .when(validations).checkArticleIsPublished(Mockito.any());

        ActionForbiddenException ex = Assertions.assertThrows(ActionForbiddenException.class, () ->
                articleService.likeArticle(0L));
        Assertions.assertEquals("Article with id %d is not published yet", ex.getMessage());
    }
}
