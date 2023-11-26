package dev.practice.mainApp.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._private.ArticlePrivateController;
import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.services.ArticlePrivateService;
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
import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticlePrivateController.class)
public class ArticlePrivateControllerTest {
    @MockBean
    private ArticlePrivateService articleService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserShortDto author = new UserShortDto(1L, "Harry");
    private final ArticleFullDto articleFull = new ArticleFullDto(1L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, 0L,  new HashSet<>(), new HashSet<>());
    private final ArticleShortDto articleShort = new ArticleShortDto(1L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), 0L, 0L, new HashSet<>(),
            new HashSet<>());
    private final ArticleNewDto articleNew = new ArticleNewDto("The empty pot",
            "Very interesting information", null);
    private final ArticleUpdateDto update = new ArticleUpdateDto();


    /*@Test
    void article_test_8_Given_validArticleAndUser_When_createArticle_Then_articleSavedStatusCreated() throws Exception {
        Mockito
                .when(articleService.createArticle(Mockito.anyLong(), Mockito.any()))
                .thenReturn(articleFull);

        mvc.perform(post("/api/v1/private/articles")
                        .content(mapper.writeValueAsString(articleNew))
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleFull.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(author.getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.created").value(notNullValue()))
                .andExpect(jsonPath("$.published").value(nullValue()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());


        Mockito.verify(articleService, Mockito.times(1))
                .createArticle(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void article_test_15_Given_validNewTitle_When_updateArticle_Then_articleUpdatedStatusOk() throws Exception {
        update.setTitle("New title");
        articleFull.setTitle(update.getTitle());
        Mockito
                .when(articleService.updateArticle(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(articleFull);

        mvc.perform(patch("/api/v1/private/articles/{articleId}", 1L)
                        .content(mapper.writeValueAsString(update))
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(update.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(author.getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.created").value(notNullValue()))
                .andExpect(jsonPath("$.published").value(nullValue()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1))
                .updateArticle(Mockito.anyLong(), Mockito.anyLong(), Mockito.any());
    }

    @Test
    void article_test_22_Given_authorizedUserArticleNotPublished_When_getArticleById_Then_throwException()
            throws Exception {
        articleFull.setStatus(ArticleStatus.MODERATING);
        Mockito
                .when(articleService.getArticleById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ActionForbiddenException.class);

        mvc.perform(get("/api/v1/private/articles/{articleId}", 1L)
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void article_test_22_Given_authorArticleNotPublished_When_getArticleById_Then_articleReturnedStatusOk()
            throws Exception {
        articleFull.setStatus(ArticleStatus.MODERATING);

        Mockito.<Optional<?>>when(articleService.getArticleById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(articleFull));

        mvc.perform(get("/api/v1/private/articles/{articleId}", 1L)
                        .header("X-Current-User-Id", 1)
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
                .andExpect(jsonPath("$.status").value("MODERATING"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1))
                .getArticleById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void article_test_23_Given_authorizedUserArticlePublished_When_getArticleById_Then_articleReturnedStatusOk()
            throws Exception {
        articleFull.setPublished(LocalDateTime.now());

        Mockito.<Optional<?>>when(articleService.getArticleById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(articleShort));

        mvc.perform(get("/api/v1/private/articles/{articleId}", 1L)
                        .header("X-Current-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleFull.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(author.getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.created").doesNotExist())
                .andExpect(jsonPath("$.published").value(notNullValue()))
                .andExpect(jsonPath("$.status").doesNotExist())
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1))
                .getArticleById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void article_test_27_Given_authorIdAndValidArticleId_When_deleteArticle_Then_articleDeletedStatusOk()
            throws Exception {
        Mockito
                .doNothing()
                .when(articleService).deleteArticle(Mockito.anyLong(), Mockito.anyLong());

        mvc.perform(delete("/api/v1/private/articles/{articleId}", 0L)
                        .header("X-Current-User-Id", 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void article_test_35_Given_userIdDefaultParameters_When_getAllArticlesByUserId_Then_returnArticlesStatusOK()
            throws Exception {
        Mockito
                .when(articleService.getAllArticlesByUserId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(List.of(articleFull));

        mvc.perform(get("/api/v1/private/articles")
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].articleId").value(1));

        Mockito.verify(articleService, Mockito.times(1))
                .getAllArticlesByUserId(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.anyString());
    }

    @Test
    void article_test_36_Given_userIdStatusCreated_When_getAllArticlesByUserId_Then_returnArticlesStatusOK()
            throws Exception {
        Mockito
                .when(articleService.getAllArticlesByUserId(
                        Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(List.of(articleFull));

        mvc.perform(get("/api/v1/private/articles")
                        .header("X-Current-User-Id", 1)
                        .param("status", "CREATED")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].articleId").value(1));

        Mockito.verify(articleService, Mockito.times(1))
                .getAllArticlesByUserId(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.anyString());
    }

    @Test
    void article_test_38_Given_articleCreated_When_publishArticle_Then_returnArticleStatusOk() throws Exception {
        articleFull.setStatus(ArticleStatus.MODERATING);
        Mockito
                .when(articleService.publishArticle(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(articleFull);

        mvc.perform(patch("/api/v1/private/articles/{articleId}/publish", 1L)
                        .header("X-Current-User-Id", 1)
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
                .andExpect(jsonPath("$.status").value("MODERATING"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1))
                .publishArticle(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void article_test_39_Given_bannedUser_When_publishArticle_Then_throwExceptionStatusForbidden() throws Exception {
        Mockito
                .when(articleService.publishArticle(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ActionForbiddenException.class);

        mvc.perform(patch("/api/v1/private/articles/{articleId}/publish", 1L)
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        Mockito.verify(articleService, Mockito.times(1))
                .publishArticle(Mockito.anyLong(), Mockito.anyLong());
    }*/


}
