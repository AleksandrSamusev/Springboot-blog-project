package dev.practice.blogproject.comment;

import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.CommentRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.impl.CommentServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @Mock
    UserRepository userRepositoryMock;
    @Mock
    CommentRepository commentRepositoryMock;
    @Mock
    ArticleRepository articleRepositoryMock;
    @InjectMocks
    CommentServiceImpl commentService;

    private final User author = new User(1L, "Harry", "Potter",
            "harryPotter", "harrypotter@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User commentAuthor = new User(2L, "John", "Doe",
            "johnDoe", "johndoe@test.test",
            LocalDate.of(1999, 11, 11), Role.USER, "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User notAnAuthor = new User(5L, "Alex", "Ferguson",
            "alexFerguson", "alexferguson@test.test",
            LocalDate.of(1980, 6, 16), Role.USER, "Hi! I'm Alex", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());


    private final User admin = new User(10L, "Kirk", "Douglas",
            "kirkDouglas", "kirkdouglas@test.test",
            LocalDate.of(1955, 3, 9), Role.ADMIN, "Hi! I'm Admin", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final UserShortDto shortUser = new UserShortDto(2L, "johnDoe");

    private final Article article = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, new HashSet<>(), new HashSet<>());

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
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(commentAuthor));
        when(articleRepositoryMock.findById(anyLong())).thenReturn(Optional.of(article));
        when(commentRepositoryMock.save(any())).thenReturn(comment);

        CommentFullDto createdComment = commentService.createComment(1L, newComment, 2L);

        assertEquals(createdComment.getComment(), newComment.getComment());
        assertEquals(createdComment.getCommentAuthor().getUsername(), commentAuthor.getUsername());
        assertEquals(createdComment.getArticleId(), article.getArticleId());
    }

    @Test
    public void comment_test2_Given_UserNotExists_When_CreateComment_Then_ResourceNotFoundException() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.createComment(1L, newComment, 2L));
        assertEquals("User with given Id = 2 not found", ex.getMessage());
    }

    @Test
    public void comment_test3_Given_ArticleNotExists_When_CreateComment_Then_ResourceNotFoundException() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.createComment(1L, newComment, 2L));
        assertEquals("Article with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test4_Given_DtoIsNull_When_CreateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.createComment(1L, null, 2L));
        assertEquals("Dto parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test5_Given_ArticleIdIsNull_When_CreateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.createComment(null, newComment, 2L));
        assertEquals("Article ID parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test6_Given_CommentAuthorIdIsNull_When_CreateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.createComment(1L, newComment, null));
        assertEquals("UserId parameter cannot be null", ex.getMessage());
    }


    @Test
    public void comment_test7_Given_ValidParameters_When_UpdateComment_Then_CommentUpdated() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepositoryMock.save(comment)).thenReturn(updatedComment);

        CommentFullDto updated = commentService.updateComment(newComment, 1L, commentAuthor.getUserId());
        assertEquals(updated.getComment(), newComment.getComment());
        assertEquals(updated.getCommentAuthor().getUsername(), commentAuthor.getUsername());
        assertEquals(updated.getArticleId(), article.getArticleId());
    }

    @Test
    public void comment_test8_Given_CommentIdNotExists_When_UpdateComment_Then_ResourceNotFoundException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.updateComment(newComment, 1L, commentAuthor.getUserId()));
        assertEquals("Comment with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test9_Given_UserIdNotExists_When_UpdateComment_Then_ResourceNotFoundException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.updateComment(newComment, 1L, commentAuthor.getUserId()));
        assertEquals("User with given Id = 2 not found", ex.getMessage());
    }

    @Test
    public void comment_test10_Given_DtoIsNull_When_UpdateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.updateComment(null, 1L, commentAuthor.getUserId()));
        assertEquals("Dto parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test6_Given_UserIdIsNull_When_UpdateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.updateComment(newComment, 1L, null));
        assertEquals("UserId parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test11_Given_CommentIdIsNull_When_UpdateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.updateComment(newComment, null, commentAuthor.getUserId()));
        assertEquals("Comment ID parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test12_Given_DtoWithNoMessage_When_UpdateComment_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.updateComment(new CommentNewDto(), 1L, commentAuthor.getUserId()));
        assertEquals("No message given", ex.getMessage());
    }

    @Test
    public void comment_test13_Given_CurrentUserIsNotCommentAuthor_When_UpdateComment_Then_InvalidParameterException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(comment));
        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                commentService.updateComment(newComment, 1L, notAnAuthor.getUserId()));
        assertEquals("Action forbidden for given user", ex.getMessage());
    }

    @Test
    public void comment_test14_Given_ValidParameters_When_DeleteComment_Then_CommentDeleted() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(commentAuthor));
        doNothing().when(commentRepositoryMock).deleteById(1L);
        commentService.deleteComment(1L, commentAuthor.getUserId());
        verify(commentRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void comment_test15_Given_UserIsAdmin_When_DeleteComment_Then_CommentDeleted() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(admin));
        doNothing().when(commentRepositoryMock).deleteById(1L);
        commentService.deleteComment(1L, admin.getUserId());
        verify(commentRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void comment_test16_Given_UserNotCommentAuthor_When_DeleteComment_Then_ActionForbiddenException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(notAnAuthor));
        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                commentService.deleteComment(1L, notAnAuthor.getUserId()));
        assertEquals("Action forbidden for given user", ex.getMessage());
    }

    @Test
    public void comment_test17_Given_CommentNotExists_When_DeleteComment_Then_ResourceNotFoundException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.deleteComment(1L, notAnAuthor.getUserId()));
        assertEquals("Comment with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test18_Given_UserNotExists_When_DeleteComment_Then_ResourceNotFoundException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.deleteComment(1L, author.getUserId()));
        assertEquals("User with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test19_Given_CommentIdIsNull_When_DeleteComment_Then_InvalidParameterException() {

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.deleteComment(null, author.getUserId()));
        assertEquals("Comment ID parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test20_Given_UserIdIsNull_When_DeleteComment_Then_InvalidParameterException() {

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.deleteComment(1L, null));
        assertEquals("UserId parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test21_Given_ExistingId_When_GetCommentById_Then_CommentReturns() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        when(commentRepositoryMock.findById(anyLong())).thenReturn(Optional.of(comment));
        CommentFullDto dto = commentService.getCommentById(comment.getCommentId());
        assertEquals(dto.getCommentId(), fullComment.getCommentId());
        assertEquals(dto.getComment(), fullComment.getComment());
        assertEquals(dto.getCommentAuthor().getUserId(), fullComment.getCommentAuthor().getUserId());
        assertEquals(dto.getArticleId(), fullComment.getArticleId()); //
    }

    @Test
    public void comment_test22_Given_CommentNotExists_When_GetCommentById_Then_ResourceNotFoundException() {
        when(commentRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.getCommentById(comment.getCommentId()));
        assertEquals("Comment with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comment_test23_Given_CommentIdIsNull_When_GetCommentById_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.getCommentById(null));
        assertEquals("ID parameter cannot be null", ex.getMessage());
    }

    @Test
    public void comment_test24_Given_ParametersValid_When_GetAllCommentsToArticle_Then_AllCommentsReturns() {
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);

        Set<Comment> comments = new HashSet<>();
        Article someArticle = new Article(1L, "Potions",
                "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
                ArticleStatus.PUBLISHED, 1450L, comments, new HashSet<>());
        Comment comment1 = new Comment(5L,
                "I found this article very interesting!!!", LocalDateTime.now(), someArticle, commentAuthor);
        Comment comment2 = new Comment(6L,
                "Another comment", LocalDateTime.now(), someArticle, commentAuthor);
        someArticle.getComments().add(comment1);
        someArticle.getComments().add(comment2);
        when(articleRepositoryMock.findById(anyLong())).thenReturn(Optional.of(someArticle));

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
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(Boolean.FALSE);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                commentService.getAllCommentsToArticle(article.getArticleId()));
        assertEquals("Article with given Id = 1 not found", ex.getMessage());
    }

    @Test
    public void comments_test25_Given_ArticleIdIsNull_When_getAllCommentsToArticle_Then_InvalidParameter() {

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                commentService.getAllCommentsToArticle(null));
        assertEquals("ID parameter cannot be null", ex.getMessage());
    }
}
