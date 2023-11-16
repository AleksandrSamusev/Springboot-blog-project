package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePublicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ArticlePublicServiceImplIntTest {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticlePublicService articleService;


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

    @Test
    void article_test_2_Given_anyUser_When_getAllArticles_Then_returnAllPublishedArticlesNewFirst() {
        userRepository.save(user);
        Article newer = articleRepository.save(article);
        Article older = articleRepository.save(article2);
        Article created = articleRepository.save(article3);

        List<ArticleShortDto> result = articleService.getAllArticles();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isInstanceOf(ArticleShortDto.class);
        assertThat(result.get(0).getArticleId()).isEqualTo(newer.getArticleId());
        assertThat(result.get(1).getArticleId()).isEqualTo(older.getArticleId());
    }


}