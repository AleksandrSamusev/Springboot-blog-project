package dev.practice.mainApp.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._public.CommentPublicController;
import dev.practice.mainApp.dtos.comment.CommentFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.services.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentPublicController.class)
public class CommentPublicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentServiceImpl commentService;

    @Autowired
    private ObjectMapper mapper;

    private final User author = new User(1L, "Harry", "Potter",
            "harryPotter", "harrypotter@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final UserShortDto shortUser = new UserShortDto(2L, "johnDoe");

    private final Article article = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, 0L, new HashSet<>(), new HashSet<>());

    private final CommentFullDto fullComment = new CommentFullDto(1L,
            "I found this article very interesting!!!", LocalDateTime.now(),
            article.getArticleId(), shortUser);

    @Test
    public void comment_test26_createCommentTest() throws Exception {
        when(commentService.getCommentById(anyLong())).thenReturn(fullComment);
        mockMvc.perform(get("/api/v1/public/comments/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.comment").value(fullComment.getComment()))
                .andExpect(jsonPath("$.articleId").value(fullComment.getArticleId()))
                .andExpect(jsonPath("$.commentAuthor.userId").value(shortUser.getUserId()));
    }

    @Test
    public void comment_test27_GetAllCommentsToArticleTest() throws Exception {

        List<CommentFullDto> list = new ArrayList<>();
        list.add(fullComment);

        when(commentService.getAllCommentsToArticle(anyLong())).thenReturn(list);
        mockMvc.perform(get("/api/v1/public/comments/articles/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].commentId").value(1))
                .andExpect(jsonPath("$.[0].commentAuthor.userId").value(2))
                .andExpect(jsonPath("$.[0].comment").value(fullComment.getComment()))
                .andExpect(jsonPath("$.[0].articleId").value(article.getArticleId()));
    }
}