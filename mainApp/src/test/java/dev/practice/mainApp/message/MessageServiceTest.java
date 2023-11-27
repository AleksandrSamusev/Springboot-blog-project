package dev.practice.mainApp.message;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.Comment;
import dev.practice.mainApp.models.Message;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.MessageRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.MessageServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepositoryMock;
    @Mock
    private UserRepository userRepository; // DO NOT DELETE! TEST 1 WILL FALL
    @Mock
    private Validations validations;
    @InjectMocks
    private MessageServiceImpl messageService;

    private final User user1 = new User(1L, "John", "Doe",
            "johnDoe", "password", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User user2 = new User(2L, "Marry", "Dawson",
            "marryDawson", "password", "merryDawson@test.test",
            LocalDate.of(1995, 6, 14), new HashSet<>(), "Hi! I'm Marry", true,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final Message fromUser1toUser2 = new Message(1L,
            "Message from user1", user1, user2, LocalDateTime.now(), false);

    private final MessageNewDto newMessage = new MessageNewDto("Message from user1");


    @Test
    public void message_test1_Given_ValidIdsAndDto_When_CreateMessage_Then_MessageCreated() {
        when(validations.checkUserExist(2L)).thenReturn(user2);
        when(userRepository.findByUsername(anyString())).thenReturn(user1);
        when(messageRepositoryMock.save(any())).thenReturn(fromUser1toUser2);

        MessageFullDto messageFullDto = messageService.createMessage(2L, "johnDoe", newMessage);

        assertEquals(messageFullDto.getMessage(), newMessage.getMessage());
        assertEquals(messageFullDto.getSender().getUserId(), user1.getUserId());
        assertEquals(messageFullDto.getRecipient().getUserId(), user2.getUserId());
        assertEquals(user2.getReceivedMessages().size(), 1);
        assertEquals(user1.getSentMessages().size(), 1);
    }

    @Test
    public void message_test2_Given_SenderIdNotExist_When_CreateMessage_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(anyLong());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.createMessage(1L, "marryDawson", newMessage));
        assertEquals("User with id 1 wasn't found", thrown.getMessage());
    }

    @Test
    public void message_test3_Given_ReceiverIdNotExist_When_CreateMessage_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(anyLong());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.createMessage(1L, "marryDawson", newMessage));
        assertEquals("User with id 2 wasn't found", thrown.getMessage());
    }

    @Test
    public void message_test4_Given_SenderIdEqualsRecipientId_When_CreateMessage_Then_ActionForbiddenException() {
        when(validations.checkUserExist(anyLong())).thenReturn(user1);
        when(userRepository.findByUsername(anyString())).thenReturn(user1);
        doThrow(new ActionForbiddenException(
                "Action forbidden for current user")).when(validations).checkSenderIsNotRecipient(anyLong(), anyLong());

        ActionForbiddenException thrown = assertThrows(ActionForbiddenException.class, () ->
                messageService.createMessage(1L, "johnDoe", newMessage));
        assertEquals("Action forbidden for current user", thrown.getMessage());
    }


    @Test
    public void message_test5_Given_ExistingMessageId_When_findMessageById_Then_MessageReturn() {

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user1);
        when(validations.checkIfMessageExists(anyLong())).thenReturn(fromUser1toUser2);

        MessageFullDto messageFullDto = messageService.findMessageById(1L, "johnDoe");

        assertEquals(messageFullDto.getMessage(), fromUser1toUser2.getMessage());
        assertEquals(messageFullDto.getMessageId(), fromUser1toUser2.getMessageId());
    }

    @Test
    public void message_test6_Given_NotExistingMessageId_When_findMessageById_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "Message with given ID = 1 not found")).when(validations).checkIfMessageExists(anyLong());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.findMessageById(1L, "johnDoe"));
        assertEquals("Message with given ID = 1 not found", thrown.getMessage());
    }

    @Test
    public void message_test7_Given_NotExistingUserId_When_findMessageById_Then_ResourceNotFoundException() {
        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenThrow(
                new ResourceNotFoundException("User with given credentials not found"));

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.findMessageById(1L, "johnDoe"));
        assertEquals("User with given credentials not found", thrown.getMessage());
    }

    @Test
    public void message_test8_Given_InvalidMessageId_When_deleteMessage_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "Message with given ID = 1 not found")).when(validations).checkIfMessageExists(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                messageService.deleteMessage(1L, "johnDoe"));
        assertEquals("Message with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void message_test9_Given_InvalidUserId_When_deleteMessage_Then_ResourceNotFoundException() {
        when(validations.checkIfMessageExists(anyLong())).thenReturn(fromUser1toUser2);
        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenThrow(
                new ResourceNotFoundException("User with given credentials not found"));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                messageService.deleteMessage(1L, "johnDoe"));
        assertEquals("User with given credentials not found", ex.getMessage());
    }
}