package dev.practice.blogproject.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._private.ArticlePrivateController;
import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.services.ArticlePrivateService;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticlePrivateController.class)
public class ArticleControllerTest {
    @MockBean
    private ArticlePrivateService articleService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserShortDto author = new UserShortDto(1L, "Harry");
    private final ArticleFullDto articleFull = new ArticleFullDto(1L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final ArticleNewDto articleNew = new ArticleNewDto("The empty pot",
            "Very interesting information", null);
    private final ArticleUpdateDto update = new ArticleUpdateDto();


    @Test
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
    void article_test_16_Given_headerWithoutUserId_When_updateArticle_Then_exceptionThrownStatusBadRequest()
            throws Exception {
        Mockito
                .when(articleService.updateArticle(null, 1L, update))
                .thenThrow(InvalidParameterException.class);

        mvc.perform(patch("/api/v1/private/articles/{articleId}", 1L)
                        .content(mapper.writeValueAsString(update))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void article_test_17_Given_pathWithoutArticleId_When_updateArticle_Then_exceptionThrownStatusBadRequest()
            throws Exception {
        Mockito
                .when(articleService.updateArticle(Mockito.anyLong(), Mockito.isNull(), Mockito.any()))
                .thenThrow(InvalidParameterException.class);

        mvc.perform(patch("/api/v1/private/articles/null")
                        .content(mapper.writeValueAsString(update))
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
