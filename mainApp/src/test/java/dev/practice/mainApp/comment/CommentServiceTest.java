package dev.practice.mainApp.comment;

import dev.practice.mainApp.dtos.comment.CommentFullDto;
import dev.practice.mainApp.dtos.comment.CommentNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.CommentRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.CommentServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private Validations validations;
    @InjectMocks
    private CommentServiceImpl commentService;

    private final User author = new User(1L, "Harry", "Potter", "harryPotter",
            "password", "harrypotter@test.test",
            LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User commentAuthor = new User(2L, "John", "Doe", "johnDoe",
            "password", "johndoe@test.test",
            LocalDate.of(1999, 11, 11), new HashSet<>(), "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User notAnAuthor = new User(5L, "Alex", "Ferguson", "alexFerguson",
            "password", "alexferguson@test.test",
            LocalDate.of(1980, 6, 16), new HashSet<>(), "Hi! I'm Alex", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());


    private final User admin = new User(10L, "Kirk", "Douglas", "kirkDouglas",
            "password", "kirkdouglas@test.test",
            LocalDate.of(1955, 3, 9), new HashSet<>(), "Hi! I'm Admin", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final UserShortDto shortUser = new UserShortDto(2L, "johnDoe");

    private final Article article = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, 0L, new HashSet<>(), new HashSet<>());

    private final Article unpublishedArticle = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.CREATED, 1450L, 0L, new HashSet<>(), new HashSet<>());

    private final Comment comment = new Comment(1L,
            "I found this article very interesting!!!", LocalDateTime.now(),
            article, commentAuthor);

    private final Comment updatedComment = new Comment(1L,
            "I found this article very interesting!!!", LocalDateTime.now(),
            article, commentAuthor);

    private final CommentNewDto newComment = new CommentNewDto("I found this article very interesting!!!");

    private final CommentFullDto fullComment = new CommentFullDto(1L,
            "I found this article very interesting!!!", LocalDateTime.now(),
            article.getArticleId(), shortUser);

    @Test
    public void comment_test1_Given_ValidParameters_When_CreateComment_Then_CommentCreated() {
        when(commentRepositoryMock.save(any())).thenReturn(comment);

        CommentFullDto createdComment = commentService.createComment(1L, newComment, "johnDoe");

        assertEquals(createdComment.getComment(), newComment.getComment());
        assertEquals(createdComment.getCommentAuthor().getUsername(), commentAuthor.getUsername());
        assertEquals(createdComment.getArticleId(), article.getArticleId());
    }

    @Test
    public void comment_test2_1_Given_ArticleNotPublished_When_CreateComment_Then_ActionForbidden() {
        doThrow(new ActionForbiddenException(
                "Article with id 1 is not published yet")).when(validations).checkArticleIsPublished(any());

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                commentService.createComment(1L, newComment, "johnDoe"));
        assertEquals("Article with id 1 is not published yet", ex.getMessage());
    }

    @Test
    public void comment_test2_2_Given_UserIsBanned_When_CreateComment_Then_ActionForbidden() {
        doThrow(new ActionForbiddenException(
                "User with id 1 is blocked")).when(validations).checkUserIsNotBanned(any());

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                commentService.createComment(1L, newComment, "johnDoe"));
        assertEquals("User with id 1 is blocked", ex.getMessage());
    }

    @Test
    public void comment_test2_Given_UserNotExists_When_CreateComment_Then_ResourceNotFoundException() {
        when(userRepositoryMock.findByUsername(any()))
                .thenThrow(new ResourceNotFoundException("User with id 2 wasn't found"));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.createComment(1L, newComment, "johnDoe"));
        assertEquals("User with id 2 wasn't found", ex.getMessage());
    }

    @Test
    public void comment_test3_Given_ArticleNotExists_When_CreateComment_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "Article with id 1 wasn't found")).when(validations).checkArticleExist(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.createComment(1L, newComment, "johnDoe"));
        assertEquals("Article with id 1 wasn't found", ex.getMessage());
    }

    @Test
    public void comment_test7_Given_ValidParameters_When_UpdateComment_Then_CommentUpdated() {
        when(validations.isCommentExists(anyLong())).thenReturn(comment);
        when(commentRepositoryMock.save(comment)).thenReturn(updatedComment);

        CommentFullDto updated = commentService.updateComment(newComment, 1L, commentAuthor.getUsername());
        assertEquals(updated.getComment(), newComment.getComment());
        assertEquals(updated.getCommentAuthor().getUsername(), commentAuthor.getUsername());
        assertEquals(updated.getArticleId(), article.getArticleId());
    }

    @Test
    public void comment_test8_Given_CommentIdNotExists_When_UpdateComment_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "Comment with given Id = 1 not found")).when(validations).isCommentExists(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.updateComment(newComment, 1L, commentAuthor.getUsername()));
        assertEquals("Comment with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test9_Given_UserIdNotExists_When_UpdateComment_Then_ResourceNotFoundException() {
        Comment comment = new Comment(1L,
                "I found this article very interesting!!!", LocalDateTime.now(),
                article, commentAuthor);
        when(validations.isCommentExists(anyLong())).thenReturn(comment);
        when(userRepositoryMock.findByUsername(anyString())).thenThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.updateComment(newComment, 1L, commentAuthor.getUsername()));
        assertEquals("User with id 2 wasn't found", ex.getMessage());
    }

    @Test
    public void comment_test13_Given_CurrentUserIsNotCommentAuthor_When_UpdateComment_Then_InvalidParameterException() {
        doThrow(new ActionForbiddenException(
                "Action forbidden for given user")).when(validations).checkUserIsCommentAuthor(any(), any());

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                commentService.updateComment(newComment, 1L, notAnAuthor.getUsername()));
        assertEquals("Action forbidden for given user", ex.getMessage());
    }

    @Test
    public void comment_test14_Given_ValidParameters_When_DeleteComment_Then_CommentDeleted() {
        when(validations.isCommentExists(anyLong())).thenReturn(comment);
        doNothing().when(commentRepositoryMock).deleteById(1L);

        commentService.deleteComment(1L, commentAuthor.getUsername());
        verify(commentRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void comment_test15_Given_UserIsAdmin_When_DeleteComment_Then_CommentDeleted() {

        User admin = new User(10L, "Kirk", "Douglas", "kirkDouglas",
                "password", "kirkdouglas@test.test",
                LocalDate.of(1955, 3, 9), new HashSet<>(), "Hi! I'm Admin", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());


        User commentAuthor = new User(2L, "John", "Doe", "johnDoe",
                "password", "johndoe@test.test",
                LocalDate.of(1999, 11, 11), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        Comment comment = new Comment(1L,
                "I found this article very interesting!!!", LocalDateTime.now(),
                article, commentAuthor);

        when(validations.isCommentExists(anyLong())).thenReturn(comment);
        when(validations.isAdmin(anyString())).thenReturn(true);
        doNothing().when(commentRepositoryMock).deleteById(1L);

        commentService.deleteComment(1L, admin.getUsername());
        verify(commentRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void comment_test16_Given_UserNotCommentAuthor_When_DeleteComment_Then_ActionForbiddenException() {
        when(validations.isCommentExists(anyLong())).thenReturn(comment);
        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                commentService.deleteComment(1L, notAnAuthor.getUsername()));
        assertEquals("Action forbidden for given user", ex.getMessage());
    }

    @Test
    public void comment_test17_Given_CommentNotExists_When_DeleteComment_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "Comment with given Id = 1 not found")).when(validations).isCommentExists(any());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.deleteComment(1L, notAnAuthor.getUsername()));
        assertEquals("Comment with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test18_Given_UserNotExists_When_DeleteComment_Then_ResourceNotFoundException() {

        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExistsByUsernameOrEmail(any());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.deleteComment(1L, author.getUsername()));
        assertEquals("User with id 1 wasn't found", ex.getMessage());
    }

    @Test
    public void comment_test21_Given_ExistingId_When_GetCommentById_Then_CommentReturns() {
        when(validations.isCommentExists(anyLong())).thenReturn(comment);

        CommentFullDto dto = commentService.getCommentById(comment.getCommentId());

        assertEquals(dto.getCommentId(), fullComment.getCommentId());
        assertEquals(dto.getComment(), fullComment.getComment());
        assertEquals(dto.getCommentAuthor().getUserId(), fullComment.getCommentAuthor().getUserId());
        assertEquals(dto.getArticleId(), fullComment.getArticleId()); //
    }

    @Test
    public void comment_test22_Given_CommentNotExists_When_GetCommentById_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "Comment with given Id = 1 not found")).when(validations).isCommentExists(any());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.getCommentById(comment.getCommentId()));
        assertEquals("Comment with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test24_Given_ParametersValid_When_GetAllCommentsToArticle_Then_AllCommentsReturns() {
        Set<Comment> comments = new HashSet<>();
        Article someArticle = new Article(1L, "Potions",
                "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
                ArticleStatus.PUBLISHED, 1450L, 0L, comments, new HashSet<>());
        Comment comment1 = new Comment(5L,
                "I found this article very interesting!!!", LocalDateTime.now(), someArticle, commentAuthor);
        Comment comment2 = new Comment(6L,
                "Another comment", LocalDateTime.now(), someArticle, commentAuthor);
        someArticle.getComments().add(comment1);
        someArticle.getComments().add(comment2);
        when(validations.checkArticleExist(anyLong())).thenReturn(someArticle);

        List<CommentFullDto> result = commentService.getAllCommentsToArticle(someArticle.getArticleId());
        List<CommentFullDto> sortedResult = result.stream().sorted(Comparator.comparing(CommentFullDto::getCommentId)).toList();

        assertEquals(comment1.getCommentId(), sortedResult.get(0).getCommentId());
        assertEquals(comment1.getComment(), sortedResult.get(0).getComment());
        assertEquals(comment1.getCommentAuthor().getUserId(), sortedResult.get(0).getCommentAuthor().getUserId());
        assertEquals(comment1.getArticle().getArticleId(), sortedResult.get(0).getArticleId());
        assertEquals(comment2.getCommentId(), sortedResult.get(1).getCommentId());
        assertEquals(comment2.getComment(), sortedResult.get(1).getComment());
        assertEquals(comment2.getCommentAuthor().getUserId(), sortedResult.get(1).getCommentAuthor().getUserId());
        assertEquals(comment2.getArticle().getArticleId(), sortedResult.get(1).getArticleId());
    }

    @Test
    public void comments_test25_Given_ArticleNotExists_When_getAllCommentsToArticle_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "Article with id 1 wasn't found")).when(validations).checkArticleExist(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.getAllCommentsToArticle(article.getArticleId()));
        assertEquals("Article with id 1 wasn't found", ex.getMessage());
    }
}
