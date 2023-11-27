package dev.practice.mainApp.user;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticlePrivateService;
import dev.practice.mainApp.services.AuthService;
import dev.practice.mainApp.services.MessageService;
import dev.practice.mainApp.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserIntegrationTest {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ArticlePrivateService articlePrivateService;
    private final UserService userService;
    private final MessageService messageService;

    @MockBean
    protected AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    protected AuthenticationManager authenticationManager;
    @MockBean
    protected HttpSecurity httpSecurity;
    @MockBean
    protected SecurityFilterChain securityFilterChain;



    @Test
    public void user_test30_When_createNewArticleByExistingUser_Then_UserHaveThisArticle() {

        UserNewDto newDto = new UserNewDto("firstName", "lastName", "username",
                "password", "email", LocalDate.of(2000, 12, 12),
                "about");

        authService.register(newDto);
        User user = userRepository.findByUsername("username");


        ArticleNewDto newArticle = new ArticleNewDto("Title", "Content", new HashSet<>());
        articlePrivateService.createArticle(user.getUsername(), newArticle);
        UserFullDto userFullDto = userService.getUserById(user.getUserId(), user.getUsername());
        assertThat(userFullDto.getArticles().size(), equalTo(1));
    }

    @Test
    public void user_test31_When_createMessage_Then_UserHaveThisMessageInSentList() {

        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1",
                "password", "email1", LocalDate.of(2011, 11, 11),
                "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2",
                "password", "email2", LocalDate.of(2012, 12, 12),
                "test2");

        authService.register(newDto1);
        authService.register(newDto2);

        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");

        User user1 = userRepository.findByUsername("username1");
        User user2 = userRepository.findByUsername("username2");

        messageService.createMessage(user2.getUserId(), user1.getUsername(), message1);
        messageService.createMessage(user1.getUserId(), user2.getUsername(), message2);
        User user1FromDb = userRepository.getReferenceById(user1.getUserId());
        User user2FromDb = userRepository.getReferenceById(user2.getUserId());

        assertThat(user1FromDb.getSentMessages().size(), is(1));
        assertThat(new ArrayList<>(user1FromDb.getSentMessages()).get(0).getMessage(),
                is(message1.getMessage()));
        assertThat(user2FromDb.getSentMessages().size(), is(1));
        assertThat(new ArrayList<>(user2FromDb.getSentMessages()).get(0).getMessage(),
                is(message2.getMessage()));
    }


    @Test
    public void user_test32_When_createMessage_Then_RecipientHasItInReceivedList() {
        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1",
                "password", "email1", LocalDate.of(2011, 11, 11),
                "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2",
                "password", "email2", LocalDate.of(2012, 12, 12),
                "test2");

        authService.register(newDto1);
        authService.register(newDto2);

        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");

        User user1 = userRepository.findByUsername("username1");
        User user2 = userRepository.findByUsername("username2");

        messageService.createMessage(user2.getUserId(), user1.getUsername(), message1);
        messageService.createMessage(user1.getUserId(), user2.getUsername(), message2);

        User user1FromDb = userRepository.getReferenceById(user1.getUserId());
        User user2FromDb = userRepository.getReferenceById(user2.getUserId());

        assertThat(user1FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user1FromDb.getReceivedMessages()).get(0).getMessage(),
                is(message2.getMessage()));
        assertThat(user2FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user2FromDb.getReceivedMessages()).get(0).getMessage(),
                is(message1.getMessage()));
    }

    @Test
    public void user_test33_When_deleteMessageByRecipient_Then_isDeletedTrue() {
        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1",
                "password", "email1", LocalDate.of(2011, 11, 11),
                "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2",
                "password", "email2", LocalDate.of(2012, 12, 12),
                "test2");

        authService.register(newDto1);
        authService.register(newDto2);

        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");

        User user1 = userRepository.findByUsername("username1");
        User user2 = userRepository.findByUsername("username2");

        MessageFullDto createdMessage1 = messageService.createMessage(user2.getUserId(),
                user1.getUsername(), message1);
        MessageFullDto createdMessage2 = messageService.createMessage(user1.getUserId(),
                user2.getUsername(), message2);

        messageService.deleteMessage(createdMessage1.getMessageId(), user2.getUsername());
        messageService.deleteMessage(createdMessage2.getMessageId(), user1.getUsername());

        User user1FromDb = userRepository.getReferenceById(user1.getUserId());
        User user2FromDb = userRepository.getReferenceById(user2.getUserId());

        assertThat(user1FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user1FromDb.getReceivedMessages()).get(0).getIsDeleted(),
                is(Boolean.TRUE));
        assertThat(user2FromDb.getReceivedMessages().size(), is(1));
        assertThat(new ArrayList<>(user2FromDb.getReceivedMessages()).get(0).getIsDeleted(),
                is(Boolean.TRUE));
    }

    @Test
    public void user_test34_When_deleteMessageByNotRecipient_Then_ActionForbiddenException() {
        UserNewDto newDto1 = new UserNewDto("firstName1", "lastName1", "username1",
                "password", "email1", LocalDate.of(2011, 11, 11),
                "test1");
        UserNewDto newDto2 = new UserNewDto("firstName2", "lastName2", "username2",
                "password", "email2", LocalDate.of(2012, 12, 12),
                "test2");

        authService.register(newDto1);
        authService.register(newDto2);

        MessageNewDto message1 = new MessageNewDto("message from user1 to user2");
        MessageNewDto message2 = new MessageNewDto("message from user2 to user1");

        User user1 = userRepository.findByUsername("username1");
        User user2 = userRepository.findByUsername("username2");

        MessageFullDto createdMessage1 = messageService.createMessage(user2.getUserId(),
                user1.getUsername(), message1);
        MessageFullDto createdMessage2 = messageService.createMessage(user1.getUserId(),
                user2.getUsername(), message2);

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                messageService.deleteMessage(createdMessage1.getMessageId(), user1.getUsername()));
        assertEquals("Action forbidden for current user", ex.getMessage());

        ActionForbiddenException ex2 = assertThrows(ActionForbiddenException.class, () ->
                messageService.deleteMessage(createdMessage2.getMessageId(), user2.getUsername()));
        assertEquals("Action forbidden for current user", ex2.getMessage());
    }
}
