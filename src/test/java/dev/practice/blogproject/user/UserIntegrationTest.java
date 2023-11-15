package dev.practice.blogproject.user;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.message.MessageNewDto;
import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.models.Message;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import dev.practice.blogproject.services.MessageService;
import dev.practice.blogproject.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final ArticlePrivateService articlePrivateService;

    @Test
    public void user_test30_When_createNewArticleByExistingUser_Then_UserHaveThisArticle() {

        UserNewDto newDto = new UserNewDto("firstName", "lastName", "username", "email", LocalDate.of(
                2000, 12, 12), "test");

        UserFullDto createdUser = userService.createUser(newDto);
        ArticleNewDto newArticle = new ArticleNewDto("Title", "Content", new HashSet<>());
        articlePrivateService.createArticle(createdUser.getUserId(), newArticle);
        UserFullDto userFullDto = userService.getUserById(createdUser.getUserId(), createdUser.getUserId());
        assertThat(userFullDto.getArticles().size(), equalTo(1));
    }

    @Test
    public void user_test31_When_createMessage_Then_UserHaveThisMessageInSentList() {

        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1", "email1", LocalDate.of(
                2011, 11, 11), "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2", "email2", LocalDate.of(
                2012, 12, 12), "test2");

        UserFullDto createdUser1 = userService.createUser(newDto1);
        UserFullDto createdUser2 = userService.createUser(newDto2);
        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");
        messageService.createMessage(createdUser2.getUserId(), createdUser1.getUserId(), message1);
        messageService.createMessage(createdUser1.getUserId(), createdUser2.getUserId(), message2);
        User user1FromDb = userRepository.getReferenceById(createdUser1.getUserId());
        User user2FromDb = userRepository.getReferenceById(createdUser2.getUserId());

        assertThat(user1FromDb.getSentMessages().size(), is(1));
        assertThat(new ArrayList<>(user1FromDb.getSentMessages()).get(0).getMessage(),
                is(message1.getMessage()));
        assertThat(user2FromDb.getSentMessages().size(), is(1));
        assertThat(new ArrayList<>(user2FromDb.getSentMessages()).get(0).getMessage(),
                is(message2.getMessage()));
    }


    @Test
    public void user_test32_When_createMessage_Then_RecipientHasItInReceivedList() {
        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1", "email1", LocalDate.of(
                2011, 11, 11), "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2", "email2", LocalDate.of(
                2012, 12, 12), "test2");

        UserFullDto createdUser1 = userService.createUser(newDto1);
        UserFullDto createdUser2 = userService.createUser(newDto2);
        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");
        messageService.createMessage(createdUser2.getUserId(), createdUser1.getUserId(), message1);
        messageService.createMessage(createdUser1.getUserId(), createdUser2.getUserId(), message2);
        User user1FromDb = userRepository.getReferenceById(createdUser1.getUserId());
        User user2FromDb = userRepository.getReferenceById(createdUser2.getUserId());

        assertThat(user1FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user1FromDb.getReceivedMessages()).get(0).getMessage(),
                is(message2.getMessage()));
        assertThat(user2FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user2FromDb.getReceivedMessages()).get(0).getMessage(),
                is(message1.getMessage()));
    }

    @Test
    public void user_test33_When_deleteMessageByRecipient_Then_isDeletedTrue() {
        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1", "email1", LocalDate.of(
                2011, 11, 11), "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2", "email2", LocalDate.of(
                2012, 12, 12), "test2");

        UserFullDto createdUser1 = userService.createUser(newDto1);
        UserFullDto createdUser2 = userService.createUser(newDto2);

        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");

        MessageFullDto createdMessage1 = messageService.createMessage(createdUser2.getUserId(),
                createdUser1.getUserId(), message1);
        MessageFullDto createdMessage2 = messageService.createMessage(createdUser1.getUserId(),
                createdUser2.getUserId(), message2);

        messageService.deleteMessage(createdMessage1.getMessageId(), createdUser2.getUserId());
        messageService.deleteMessage(createdMessage2.getMessageId(), createdUser1.getUserId());

        User user1FromDb = userRepository.getReferenceById(createdUser1.getUserId());
        User user2FromDb = userRepository.getReferenceById(createdUser2.getUserId());

        assertThat(user1FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user1FromDb.getReceivedMessages()).get(0).getIsDeleted(),
                is(Boolean.TRUE));
        assertThat(user2FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user2FromDb.getReceivedMessages()).get(0).getIsDeleted(),
                is(Boolean.TRUE));
    }

    @Test
    public void user_test34_When_deleteMessageByNotRecipient_Then_ActionForbiddenException() {
        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1", "email1", LocalDate.of(
                2011, 11, 11), "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2", "email2", LocalDate.of(
                2012, 12, 12), "test2");

        UserFullDto createdUser1 = userService.createUser(newDto1);
        UserFullDto createdUser2 = userService.createUser(newDto2);

        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");

        MessageFullDto createdMessage1 = messageService.createMessage(createdUser2.getUserId(),
                createdUser1.getUserId(), message1);
        MessageFullDto createdMessage2 = messageService.createMessage(createdUser1.getUserId(),
                createdUser2.getUserId(), message2);

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, ()->
                messageService.deleteMessage(createdMessage1.getMessageId(), createdUser1.getUserId()));
        assertEquals("Action forbidden for current user", ex.getMessage());

        ActionForbiddenException ex2 = assertThrows(ActionForbiddenException.class, ()->
                messageService.deleteMessage(createdMessage2.getMessageId(), createdUser2.getUserId()));
        assertEquals("Action forbidden for current user", ex2.getMessage());
    }
}
