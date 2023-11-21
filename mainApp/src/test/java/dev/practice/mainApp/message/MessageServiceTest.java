package dev.practice.mainApp.message;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.MessageRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.MessageServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceTest {

    @Mock
    MessageRepository messageRepositoryMock;
    @Mock
    UserRepository userRepositoryMock;

    @InjectMocks
    MessageServiceImpl messageService;

    private final User user1 = new User(1L, "John", "Doe",
            "johnDoe", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User user2 = new User(2L, "Marry", "Dawson",
            "marryDawson", "merryDawson@test.test",
            LocalDate.of(1995, 6, 14), Role.USER, "Hi! I'm Marry", true,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final Message fromUser1toUser2 = new Message(1L,
            "Message from user1", user1, user2, LocalDateTime.now(), false);

    private final MessageNewDto newMessage = new MessageNewDto("Message from user1");


    @Test
    public void message_test1_Given_ValidIdsAndDto_When_CreateMessage_Then_MessageCreated() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepositoryMock.findById(2L)).thenReturn(Optional.of(user2));
        when(messageRepositoryMock.save(any())).thenReturn(fromUser1toUser2);

        MessageFullDto messageFullDto = messageService.createMessage(2L, 1L, newMessage);

        assertEquals(messageFullDto.getMessage(), newMessage.getMessage());
        assertEquals(messageFullDto.getSender().getUserId(), user1.getUserId());
        assertEquals(messageFullDto.getRecipient().getUserId(), user2.getUserId());
        assertEquals(user2.getReceivedMessages().size(), 1);
        assertEquals(user1.getSentMessages().size(), 1);
    }

    @Test
    public void message_test2_Given_SenderIdNotExist_When_CreateMessage_Then_ResourceNotFoundException() {
        when(userRepositoryMock.existsById(1L)).thenReturn(false);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.createMessage(1L, 2L, newMessage));
        assertEquals("User with given ID = 1 not found", thrown.getMessage());
    }

    @Test
    public void message_test3_Given_ReceiverIdNotExist_When_CreateMessage_Then_ResourceNotFoundException() {
        when(userRepositoryMock.existsById(1L)).thenReturn(true);
        when(userRepositoryMock.existsById(2L)).thenReturn(false);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.createMessage(1L, 2L, newMessage));
        assertEquals("User with given ID = 2 not found", thrown.getMessage());
    }

    @Test
    public void message_test4_Given_SenderIdEqualsReceiverId_When_CreateMessage_Then_ActionForbiddenException() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);

        ActionForbiddenException thrown = assertThrows(ActionForbiddenException.class, () ->
                messageService.createMessage(1L, 1L, newMessage));
        assertEquals("Action forbidden for current user", thrown.getMessage());
    }


    @Test
    public void message_test5_Given_ExistingMessageId_When_findMessageById_Then_MessageReturn() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(messageRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(messageRepositoryMock.findById(anyLong())).thenReturn(Optional.of(fromUser1toUser2));

        MessageFullDto messageFullDto = messageService.findMessageById(1L, 1L);

        assertEquals(messageFullDto.getMessage(), fromUser1toUser2.getMessage());
        assertEquals(messageFullDto.getMessageId(), fromUser1toUser2.getMessageId());
    }

    @Test
    public void message_test6_Given_NotExistingMessageId_When_findMessageById_Then_ResourceNotFoundException() {
        when(messageRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.findMessageById(1L, 1L));
        assertEquals("Message with given ID = 1 not found", thrown.getMessage());
    }

    @Test
    public void message_test7_Given_NotExistingUserId_When_findMessageById_Then_ResourceNotFoundException() {
        when(messageRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(userRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                messageService.findMessageById(1L, 1L));
        assertEquals("User with given ID = 1 not found", thrown.getMessage());
    }

    @Test
    public void message_test8_Given_InvalidMessageId_When_deleteMessage_Then_ResourceNotFoundException() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(messageRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                messageService.deleteMessage(1L, 1L));
        assertEquals("Message with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void message_test9_Given_InvalidUserId_When_deleteMessage_Then_ResourceNotFoundException() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                messageService.deleteMessage(1L, 1L));
        assertEquals("User with given ID = 1 not found", ex.getMessage());
    }


}