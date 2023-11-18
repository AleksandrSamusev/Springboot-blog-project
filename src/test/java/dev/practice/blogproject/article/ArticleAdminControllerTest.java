package dev.practice.blogproject.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._admin.ArticleAdminController;
import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.services.ArticleAdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticleAdminController.class)
public class ArticleAdminControllerTest {
    @MockBean
    private ArticleAdminService articleService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserShortDto author = new UserShortDto(1L, "Harry");
    private final ArticleFullDto articleFull = new ArticleFullDto(1L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());

    @Test
    void article_test_3_Given_adminUserExist_When_getAllArticlesByUserId_Then_returnArticlesStatusOK()
            throws Exception {
        Mockito
                .when(articleService.getAllArticlesByUserId(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(List.of(articleFull));

        mvc.perform(get("/api/v1/admin/articles/users/{authorId}", 1L)
                        .header("X-Current-User-Id", 0L)
                        .param("status", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].articleId").value(1));

        Mockito.verify(articleService, Mockito.times(1))
                .getAllArticlesByUserId(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(),
                        Mockito.any(), Mockito.anyString());
    }

    @Test
    void article_test_4_Given_notAdminUserExist_When_getAllArticlesByUserId_Then_throwExceptionStatusForbidden()
            throws Exception {
        Mockito
                .when(articleService.getAllArticlesByUserId(
                        Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenThrow(ActionForbiddenException.class);

        mvc.perform(get("/api/v1/admin/articles/users/{authorId}", 1L)
                        .header("X-Current-User-Id", 0L)
                        .param("status", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
