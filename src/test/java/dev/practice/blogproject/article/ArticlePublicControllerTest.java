package dev.practice.blogproject.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._public.ArticlePublicController;
import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.services.ArticlePublicService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArticlePublicController.class)
public class ArticlePublicControllerTest {
    @MockBean
    private ArticlePublicService articleService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserShortDto author = new UserShortDto(1L, "Harry");
    private final ArticleShortDto articleShort = new ArticleShortDto(1L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), 0L, new HashSet<>(),
            new HashSet<>());
    private final ArticleShortDto articleShort2 = new ArticleShortDto(2L, "The pretty pot",
            "Very interesting information", author, LocalDateTime.now().minusDays(2), 0L, new HashSet<>(),
            new HashSet<>());

    @Test
    void article_test_3_Given_anyUser_When_getAllArticles_Then_returnAllPublishedStatusOk() throws Exception {
        Mockito
                .when(articleService.getAllArticles())
                .thenReturn(List.of(articleShort, articleShort2));

        mvc.perform(get("/api/v1/public/articles")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(articleService, Mockito.times(1)).getAllArticles();
    }
}
