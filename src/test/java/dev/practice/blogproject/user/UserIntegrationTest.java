package dev.practice.blogproject.user;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.services.ArticlePrivateService;
import dev.practice.blogproject.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.HashSet;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private final ArticlePrivateService articlePrivateService;

    @Test
    public void When_createNewArticleByExistingUser_Then_UserHaveThisArticle() {
        UserNewDto newDto = new UserNewDto("firstName", "lastName", "username", "email", LocalDate.of(
                2000, 12, 12), "test");
        UserFullDto createdUser = userService.createUser(newDto);

        ArticleNewDto newArticle = new ArticleNewDto("Title", "Content", new HashSet<>());
        ArticleFullDto createdArticle = articlePrivateService
                .createArticle(createdUser.getUserId(), newArticle);

        UserFullDto userFullDto = userService.getUserById(createdUser.getUserId(), createdUser.getUserId());

        assertThat(userFullDto.getArticles().size(), equalTo(1));


    }


}
