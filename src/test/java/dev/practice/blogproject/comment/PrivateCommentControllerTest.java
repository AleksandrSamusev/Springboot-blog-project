package dev.practice.blogproject.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._private.PrivateCommentController;
import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.services.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateCommentController.class)
public class PrivateCommentControllerTest {

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
            ArticleStatus.PUBLISHED, 1450L, new HashSet<>(), new HashSet<>());

    private final CommentFullDto fullComment = new CommentFullDto(1L,
            "I found this article very interesting!!!",
            LocalDateTime.now(), article.getArticleId(), shortUser);

    private final CommentFullDto fullUpdatedComment = new CommentFullDto(2L,
            "Updated!", LocalDateTime.now(), article.getArticleId(), shortUser);

    private final CommentNewDto newDto = new CommentNewDto("I found this article very interesting!!!");

    private final CommentNewDto dtoForUpdate = new CommentNewDto("Updated!");

    @Test
    public void comment_test28_CreateCommentTest() throws Exception {
        when(commentService.createComment(anyLong(), any(), anyLong())).thenReturn(fullComment);
        mockMvc.perform(post("/api/v1/private/comments/article/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1))
                .andExpect(jsonPath("$.comment").value(newDto.getComment()))
                .andExpect(jsonPath("$.articleId").value(article.getArticleId()))
                .andExpect(jsonPath("$.commentAuthor.userId").value(2));
    }

    @Test
    public void comment_test29_UpdateCommentTest() throws Exception {
        when(commentService.updateComment(any(), anyLong(), anyLong())).thenReturn(fullUpdatedComment);
        mockMvc.perform(patch("/api/v1/private/comments/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(dtoForUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(2))
                .andExpect(jsonPath("$.comment").value(dtoForUpdate.getComment()))
                .andExpect(jsonPath("$.articleId").value(article.getArticleId()))
                .andExpect(jsonPath("$.commentAuthor.userId").value(2));
    }

    @Test
    public void comment_test30_DeleteCommentTest() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());
        mockMvc.perform(delete("/api/v1/private/comments/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }

}
