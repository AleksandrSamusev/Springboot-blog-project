package dev.practice.mainApp.article;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticleAdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

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
    private final ArticleAdminService articleService;
    private final RoleRepository roleRepository;

    @MockBean
    protected AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    protected AuthenticationManager authenticationManager;
    @MockBean
    protected HttpSecurity httpSecurity;
    @MockBean
    protected SecurityFilterChain securityFilterChain;


    private final Role adminRole = new Role(null, "ROLE_ADMIN");
    private final Role userRole = new Role(null, "ROLE_USER");
    private final User user = new User(null, "Harry", "Potter", "HP", "user-password",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), new HashSet<>(), null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final User user2 = new User(null, "Admin", "Admin", "ADMIN", "admin-password",
            "admin@gmail.com", LocalDate.of(1990, 9, 10), new HashSet<>(), null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now().minusDays(5),
            ArticleStatus.PUBLISHED, 0L, 1450L, new HashSet<>(), new HashSet<>());

    @Test
    void article_test_2_Given_adminAndExistUser_When_getAllArticlesByUserId_Then_returnAllUserArticles() {
        Role savedAdminRole = roleRepository.save(adminRole);
        Role savedUserRole = roleRepository.save(userRole);

        user.getRoles().add(savedUserRole);
        user2.getRoles().add(savedAdminRole);
        User author = userRepository.save(user);
        userRepository.save(user2);

        Article savedArticle = articleRepository.save(article);
        author.getArticles().add(savedArticle);
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(user2.getUsername(),
                author.getUserId(), 0, 10, "ALL");

        assertThat(result.get(0)).isInstanceOf(ArticleFullDto.class);
        assertThat(result.size()).isEqualTo(1);
    }
}
