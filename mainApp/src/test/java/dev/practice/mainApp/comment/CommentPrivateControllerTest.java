package dev.practice.mainApp.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.config.SecurityConfig;
import dev.practice.mainApp.controllers._private.CommentPrivateController;
import dev.practice.mainApp.dtos.comment.CommentFullDto;
import dev.practice.mainApp.dtos.comment.CommentNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationEntryPoint;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.impl.CommentServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(CommentPrivateController.class)
public class CommentPrivateControllerTest {
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
    private CommentServiceImpl commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final Role roleUser = new Role(2L, "ROLE_USER");
    private final User author = new User(1L, "Harry", "Potter", "password",
            "harryPotter", "harrypotter@test.test", LocalDate.of(2000, 12, 27),
            Set.of(roleUser), "Hi! I'm Harry", false, new HashSet<Message>(), new HashSet<Message>(),
            new HashSet<Article>(), new HashSet<Comment>());

    private final UserShortDto shortUser = new UserShortDto(2L, "johnDoe");

    private final Article article = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, 0L, new HashSet<>(), new HashSet<>());

    private final CommentFullDto fullComment = new CommentFullDto(1L,
            "I found this article very interesting!!!",
            LocalDateTime.now(), article.getArticleId(), shortUser);

    private final CommentFullDto fullUpdatedComment = new CommentFullDto(2L,
            "Updated!", LocalDateTime.now(), article.getArticleId(), shortUser);

    private final CommentNewDto newDto = new CommentNewDto("I found this article very interesting!!!");

    private final CommentNewDto dtoForUpdate = new CommentNewDto("Updated!");


    @Test
    @WithMockUser
    public void comment_test28_CreateCommentTest() throws Exception {
        when(commentService.createComment(anyLong(), any(), anyString())).thenReturn(fullComment);
        mockMvc.perform(post("/api/v1/private/comments/articles/1")
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

    @WithMockUser
    @Test
    public void comment_test29_UpdateCommentTest() throws Exception {
        when(commentService.updateComment(any(), anyLong(), anyString())).thenReturn(fullUpdatedComment);
        mockMvc.perform(patch("/api/v1/private/comments/1")
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

    @WithMockUser
    @Test
    public void comment_test30_DeleteCommentTest() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyString());
        mockMvc.perform(delete("/api/v1/private/comments/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void comment_test31_Given_MessageIsNull_When__CreateComment_Then_BadRequest() throws Exception {

        CommentNewDto dto = new CommentNewDto(null);
        when(commentService.createComment(anyLong(), any(), anyString())).thenReturn(fullComment);
        mockMvc.perform(post("/api/v1/private/comments/articles/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Comment cannot be blank")));
    }

    @WithMockUser
    @Test
    public void comment_test32_Given_MessageLength510Chars_When__CreateComment_Then_BadRequest() throws Exception {

        CommentNewDto dto = new CommentNewDto("012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");

        when(commentService.createComment(anyLong(), any(), anyString())).thenReturn(fullComment);
        mockMvc.perform(post("/api/v1/private/comments/articles/1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Comment length should be 500 chars max")));
    }

}
