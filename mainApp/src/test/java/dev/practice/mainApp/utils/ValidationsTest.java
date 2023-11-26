package dev.practice.mainApp.utils;

import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ValidationsTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MessageRepository messageRepository;
    @InjectMocks
    private Validations validations;

    private final User user = new User(0L, "Harry", "Potter", "user",
            "password","hp@gmail.com", LocalDate.of(1981, 7, 31),
            new HashSet<>(), null,false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final User admin = new User(1L, "Ron", "Weasley", "admin",
            "password", "rw@gmail.com", LocalDate.of(1981, 9, 16),
            new HashSet<>(), null,false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final Article savedArticle = new Article(0L, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, 0L, new HashSet<>(), new HashSet<>());
    private final Tag tag = new Tag(0L, "Potions", new HashSet<>());
    private final Comment comment = new Comment(0L, "I found this article very interesting!!!",
            LocalDateTime.now(), savedArticle, user);
    private final Message fromAdminToUser = new Message(1L,
            "Message from user1", admin, user, LocalDateTime.now(), false);

    @Test
    public void val_test_1_Given_existUser_When_checkUserExist_Then_returnUser() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        User result = validations.checkUserExist(0L);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getUserId()).isEqualTo(user.getUserId());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void val_test_2_Given_userNotExist_When_checkUserExist_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> validations.checkUserExist(1L));
        assertEquals("User with id 1 wasn't found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void val_test_3_Given_existUserNotBanned_When_checkUserIsNotBanned_Then_doNothing() {
        validations.checkUserIsNotBanned(user);
    }

    @Test
    public void val_test_4_Given_existUserBanned_When_checkUserIsNotBanned_Then_throwException() {
        user.setIsBanned(true);

        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.checkUserIsNotBanned(user));
        assertEquals("User with id 0 is blocked", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

/*    @Test
    public void val_test_5_Given_userIsAdmin_When_checkUserIsAdmin_Then_doNothing() {
        user.setRole(Role.ADMIN);
        validations.checkUserIsAdmin(user);
    }*/

/*    @Test
    public void val_test_6_Given_userIsNotAdmin_When_checkUserIsAdmin_Then_throwException() {
        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.checkUserIsAdmin(user));
        assertEquals("User with id 0 is not ADMIN. Access is forbidden", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }*/

    @Test
    public void val_test_6_Given_userIsAuthor_When_checkUserIsAuthor_Then_doNothing() {
        validations.checkUserIsAuthor(savedArticle, user.getUsername());
    }

    @Test
    public void val_test_7_Given_userIsNotAuthor_When_checkUserIsAuthor_Then_throwException() {
        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.checkUserIsAuthor(savedArticle, "abc"));
        assertEquals("Article with id 0 is not belongs to user with username abc. Action is forbidden",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    public void val_test_8_Given_userIsAdmin_When_isUserAuthorized_Then_doNothing() {
        validations.isUserAuthorized(admin.getUserId(), admin);
    }

    @Test
    public void val_test_9_Given_userEqualsCurrentUser_When_isUserAuthorized_Then_doNothing() {
        validations.isUserAuthorized(user.getUserId(), user);
    }

    @Test
    public void val_test_9_Given_userIsNotAdminNotEqualsCurrentUser_When_isUserAuthorized_Then_throwException() {
        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.isUserAuthorized(105L, user));
        assertEquals("Action forbidden for current user", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    public void val_test_10_Given_existArticle_When_checkArticleExist_Then_returnArticle() {
        Mockito
                .when(articleRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(savedArticle));

        Article result = validations.checkArticleExist(savedArticle.getArticleId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Article.class);
        assertThat(result.getArticleId()).isEqualTo(savedArticle.getArticleId());
        assertThat(result.getTitle()).isEqualTo(savedArticle.getTitle());
    }

    @Test
    public void val_test_11_Given_articleNotExist_When_checkArticleExist_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> validations.checkArticleExist(2L));
        assertEquals("Article with id 2 wasn't found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void val_test_12_Given_publishedArticle_When_checkArticleIsPublished_Then_doNothing() {
        savedArticle.setStatus(ArticleStatus.PUBLISHED);
        validations.checkArticleIsPublished(savedArticle);
    }

    @Test
    public void val_test_12_Given_articleNotPublished_When_checkArticleIsPublished_Then_throwException() {
        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.checkArticleIsPublished(savedArticle));
        assertEquals("Article with id 0 is not published yet", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    public void val_test_13_Given_titleExistTheSameArticle_When_checkTitleNotExist_Then_doNothing() {
        Mockito
                .when(articleRepository.findArticlesByTitleIgnoreCase(Mockito.anyString()))
                .thenReturn(savedArticle);

        validations.checkTitleNotExist(savedArticle.getTitle(), savedArticle.getArticleId());
    }

    @Test
    public void val_test_14_Given_titleNotExist_When_checkTitleNotExist_Then_doNothing() {
        validations.checkTitleNotExist(savedArticle.getTitle(), null);
    }

    @Test
    public void val_test_15_Given_titleExistNotTheSameArticle_When_checkArticleIsPublished_Then_throwException() {
        Mockito
                .when(articleRepository.findArticlesByTitleIgnoreCase(Mockito.anyString()))
                .thenReturn(savedArticle);

        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> validations.checkTitleNotExist(savedArticle.getTitle(), 105L));
        assertEquals(String.format("Article with title %s already exist", savedArticle.getTitle()),
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(InvalidParameterException.class);
    }

    @Test
    public void val_test_15_Given_titleExist_When_checkArticleIsPublished_Then_throwException() {
        Mockito
                .when(articleRepository.findArticlesByTitleIgnoreCase(Mockito.anyString()))
                .thenReturn(savedArticle);

        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> validations.checkTitleNotExist(savedArticle.getTitle(), null));
        assertEquals(String.format("Article with title %s already exist", savedArticle.getTitle()),
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(InvalidParameterException.class);
    }

    @Test
    public void val_test_16_Given_tagExist_When_isTagExist_Then_returnTag() {
        Mockito
                .when(tagRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(tag));

        Tag result = validations.isTagExists(tag.getTagId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Tag.class);
        assertThat(result.getTagId()).isEqualTo(tag.getTagId());
        assertThat(result.getName()).isEqualTo(tag.getName());
    }

    @Test
    public void val_test_17_Given_tagNotExist_When_isTagExist_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> validations.isTagExists(2L));
        assertEquals("Tag with given ID = 2 not found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void val_test_18_Given_commentExist_When_isCommentExists_Then_returnComment() {
        Mockito
                .when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(comment));

        Comment result = validations.isCommentExists(comment.getCommentId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Comment.class);
        assertThat(result.getCommentId()).isEqualTo(comment.getCommentId());
        assertThat(result.getComment()).isEqualTo(comment.getComment());
    }

    @Test
    public void val_test_19_Given_commentNotExist_When_isCommentExists_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> validations.isCommentExists(2L));
        assertEquals("Comment with given Id = 2 not found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void val_test_20_Given_userIsCommentAuthor_When_checkUserIsCommentAuthor_Then_doNothing() {
        validations.checkUserIsCommentAuthor(user, comment);
    }

    @Test
    public void val_test_21_Given_userIsNotCommentAuthor_When_checkUserIsCommentAuthor_Then_throwException() {
        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.checkUserIsCommentAuthor(admin, comment));
        assertEquals("Action forbidden for given user", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }

    @Test
    public void val_test_22_Given_messageExist_When_checkIfMessageExists_Then_returnMessage() {
        Mockito
                .when(messageRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(fromAdminToUser));

        Message result = validations.checkIfMessageExists(fromAdminToUser.getMessageId());

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Message.class);
        assertThat(result.getMessageId()).isEqualTo(fromAdminToUser.getMessageId());
        assertThat(result.getMessage()).isEqualTo(fromAdminToUser.getMessage());
    }

    @Test
    public void val_test_22_Given_messageNotExist_When_checkIfMessageExists_Then_throwException() {
        final ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class,
                () -> validations.checkIfMessageExists(2L));
        assertEquals("Message with given ID = 2 not found", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void val_test_23_Given_senderIsNotRecipient_When_checkSenderIsNotRecipient_Then_doNothing() {
        validations.checkSenderIsNotRecipient(admin.getUserId(), user.getUserId());
    }

    @Test
    public void val_test_24_Given_senderIsRecipient_When_checkSenderIsNotRecipient_Then_throwException() {
        final ActionForbiddenException exception = Assertions.assertThrows(ActionForbiddenException.class,
                () -> validations.checkSenderIsNotRecipient(user.getUserId(), user.getUserId()));
        assertEquals("Action forbidden for current user", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(ActionForbiddenException.class);
    }
}