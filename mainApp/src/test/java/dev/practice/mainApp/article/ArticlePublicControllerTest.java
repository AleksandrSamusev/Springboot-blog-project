package dev.practice.mainApp.article;

import dev.practice.mainApp.config.SecurityConfig;
import dev.practice.mainApp.controllers._public.ArticlePublicController;
import dev.practice.mainApp.dtos.article.ArticleShortDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationEntryPoint;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.ArticlePublicService;
import dev.practice.mainApp.utils.Validations;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = ArticlePublicController.class)
public class ArticlePublicControllerTest {
    @MockBean
    protected RoleRepository roleRepository;
    @MockBean
    protected Validations validations;
    @MockBean
    protected JWTTokenProvider tokenProvider;
    @MockBean
    protected UserDetailsService userDetailsService;
    @MockBean
    protected JWTAuthenticationEntryPoint entryPoint;
    @MockBean
    private ArticlePublicService articleService;

    @Autowired
    private MockMvc mvc;


    private final UserShortDto author = new UserShortDto(1L, "Harry");
    private final ArticleShortDto articleShort = new ArticleShortDto(1L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), 0L, 0L, new HashSet<>(),
            new HashSet<>());
    private final ArticleShortDto articleShort2 = new ArticleShortDto(2L, "The pretty pot",
            "Very interesting information", author, LocalDateTime.now().minusDays(2), 0L, 0L, new HashSet<>(),
            new HashSet<>());
    private final List<ArticleShortDto> list = List.of(articleShort, articleShort2);

    @Test
    void article_test_3_Given_anyUser_When_getAllArticles_Then_returnAllPublishedStatusOk() throws Exception {
        Mockito
                .when(articleService.getAllArticles(Mockito.any(), Mockito.any()))
                .thenReturn(List.of(articleShort, articleShort2));

        mvc.perform(get("/api/v1/public/articles")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(articleService, Mockito.times(1)).getAllArticles(
                Mockito.any(), Mockito.any());
    }

    @Test
    void article_test_8_Given_anyUserArticleExist_When_getArticleById_Then_returnArticleStatusOk() throws Exception {
        Mockito
                .when(articleService.getArticleById(Mockito.anyLong(), Mockito.any()))
                .thenReturn(articleShort);

        mvc.perform(get("/api/v1/public/articles/{articleId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleShort.getTitle()))
                .andExpect(jsonPath("$.content").value(articleShort.getContent()))
                .andExpect(jsonPath("$.author.userId").value(author.getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.created").doesNotExist())
                .andExpect(jsonPath("$.published").value(notNullValue()))
                .andExpect(jsonPath("$.status").doesNotExist())
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(articleService, Mockito.times(1)).getArticleById(Mockito.anyLong()
                , Mockito.any());
    }

    @Test
    void article_test_11_Given_anyUserAuthorExist_When_getAllArticlesByUserId_Then_returnPublishedArticlesStatusOk()
            throws Exception {
        Mockito
                .when(articleService.getAllArticlesByUserId(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(articleShort));

        mvc.perform(get("/api/v1/public/articles/users/{userId}", 0L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(articleService, Mockito.times(1)).getAllArticlesByUserId(
                Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void article_test_15_Given_ExistingArticle_When_LikeArticle_Then_ReturnArticle_200_OK()
            throws Exception {
        Mockito
                .when(articleService.likeArticle(Mockito.anyLong()))
                .thenReturn(articleShort);

        mvc.perform(patch("/api/v1/public/articles/{articleId}/like", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void article_test_16_Given_ArticleNotExists_When_LikeArticle_Then_404_NOT_FOUND()
            throws Exception {
        Mockito
                .when(articleService.likeArticle(Mockito.anyLong()))
                .thenThrow(new ResourceNotFoundException("Article with id 0 wasn't found"));

        mvc.perform(patch("/api/v1/public/articles/{articleId}/like", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void article_test_17_Given_ArticleNotPublished_When_LikeArticle_Then_403_FORBIDDEN()
            throws Exception {
        Mockito
                .when(articleService.likeArticle(Mockito.anyLong()))
                .thenThrow(new ActionForbiddenException("Article with id 0 is not published yet"));

        mvc.perform(patch("/api/v1/public/articles/{articleId}/like", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void article_test_41_Given_ValidId_When_getAllArticlesByTag_Then_ReturnList() throws Exception {
        Mockito
                .when(articleService.getAllArticlesByTag(Mockito.anyLong()))
                .thenReturn(list);

        mvc.perform(get("/api/v1/public/articles/tags/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
