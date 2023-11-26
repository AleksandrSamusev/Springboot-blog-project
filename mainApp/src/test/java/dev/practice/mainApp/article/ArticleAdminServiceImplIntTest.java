package dev.practice.mainApp.article;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.*;
import dev.practice.mainApp.services.ArticleAdminService;
import dev.practice.mainApp.services.CommentService;
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
public class ArticleAdminServiceImplIntTest {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ArticleAdminService articleService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final RoleRepository roleRepository;


    private final Role adminRole = new Role(null, "ROLE_ADMIN");
    private final Role userRole = new Role(null, "ROLE_USER");
    private final User user = new User(null, "Harry", "Potter", "HP", "user-password",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), new HashSet<>(), null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final User user2 = new User(null, "Admin", "Admin", "ADMIN", "admin-password",
            "admin@gmail.com", LocalDate.of(1990, 9, 10), new HashSet<>(), null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final Tag tag1 = new Tag(null, "Potions", new HashSet<>());
    private final Tag tag2 = new Tag(null, "Cat", new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final ArticleNewDto newArticle2 = new ArticleNewDto("Pot", "Interesting information",
            new HashSet<>());
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now().minusDays(5),
            ArticleStatus.PUBLISHED, 0L, 1450L, new HashSet<>(), new HashSet<>());
    private final Article article2 = new Article(null, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), null,  ArticleStatus.CREATED,
            0L, 0L, new HashSet<>(), new HashSet<>());

    @Test
    void article_test_2_Given_adminAndExistUser_When_getAllArticlesByUserId_Then_returnAllUserArticles() {
        Role savedAdminRole = roleRepository.save(adminRole);
        Role savedUserRole = roleRepository.save(userRole);

        user.getRoles().add(savedUserRole);
        user2.getRoles().add(savedAdminRole);
        User author = userRepository.save(user);
        User admin = userRepository.save(user2);

        articleRepository.save(article);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(
                author.getUserId(), 0, 10, "ALL");

        assertThat(result.get(0)).isInstanceOf(ArticleFullDto.class);
        assertThat(result.size()).isEqualTo(1);
    }
}
