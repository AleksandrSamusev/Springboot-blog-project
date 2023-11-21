package dev.practice.mainApp.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._admin.ArticleAdminController;
import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.services.ArticleAdminService;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Test
    void article_test_8_Given_adminPublishFalse_When_publishArticle_Then_ReturnArticleStatusOk() throws Exception {
        articleFull.setStatus(ArticleStatus.REJECTED);
        Mockito
                .when(articleService.publishArticle(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(articleFull);

        mvc.perform(patch("/api/v1/admin/articles/{articleId}/publish", 1L)
                        .header("X-Current-User-Id", 1)
                        .param("publish", String.valueOf(false))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleFull.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(author.getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.created").value(notNullValue()))
                .andExpect(jsonPath("$.published").value(nullValue()))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1))
                .publishArticle(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }

    @Test
    void article_test_9_Given_adminPublishTrue_When_publishArticle_Then_ReturnArticleStatusOk() throws Exception {
        articleFull.setStatus(ArticleStatus.PUBLISHED);
        articleFull.setPublished(LocalDateTime.now());
        Mockito
                .when(articleService.publishArticle(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(articleFull);

        mvc.perform(patch("/api/v1/admin/articles/{articleId}/publish", 1L)
                        .header("X-Current-User-Id", 1)
                        .param("publish", String.valueOf(false))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleFull.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(author.getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.created").value(notNullValue()))
                .andExpect(jsonPath("$.published").value(notNullValue()))
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1))
                .publishArticle(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean());
    }
}