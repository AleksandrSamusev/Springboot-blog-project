package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.services.impl.ArticlePublicServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ArticlePublicServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticlePublicServiceImpl articleService;

    private final User author = new User(0L, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final User author2 = new User(1L, "Ron", "Weasley", "RW",
            "rw@gmail.com", LocalDate.of(1981, 9, 16), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final Article savedArticle = new Article(0L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 0L, new HashSet<>(), new HashSet<>());
    private final Article savedArticle2 = new Article(1L, "A pretty cat",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now().minusDays(2),
            ArticleStatus.PUBLISHED, 0L, new HashSet<>(), new HashSet<>());


    @Test
    void article_test_1_Given_anyUser_When_getAllArticles_Then_returnAllPublishedArticles() {
        Mockito
                .when(articleRepository.findArticlesByStatusOrderByPublishedDesc(ArticleStatus.PUBLISHED, PageRequest.of(0, 10)))
                .thenReturn(List.of(savedArticle2, savedArticle));

        List<ArticleShortDto> result = articleService.getAllArticles(0, 10);

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void article_test_4_Given_anyUserArticleExist_When_getArticleById_Then_returnArticle() {
        Mockito
                .when(articleRepository.findById(0L))
                .thenReturn(Optional.of(savedArticle));

        ArticleShortDto result = articleService.getArticleById(0L);

        assertThat(result.getArticleId()).isEqualTo(0);
    }
}
