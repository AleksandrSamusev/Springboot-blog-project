package dev.practice.blogproject.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._private.MessagePrivateController;
import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.message.MessageNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.services.impl.MessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessagePrivateController.class)
public class MessagePrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageServiceImpl messageService;

    @Autowired
    private ObjectMapper mapper;

    private final UserShortDto user1 = new UserShortDto(1L, "username1");
    private final UserShortDto user2 = new UserShortDto(2L, "username2");

    @Test
    public void message_test10_createMessageTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        MessageNewDto newDto = new MessageNewDto("new message");

        when(messageService.createMessage(anyLong(), anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .header("X-Current-User-Id", 2)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId").value(1L))
                .andExpect(jsonPath("$.message").value("new message"))
                .andExpect(jsonPath("$.recipient.userId").value(user1.getUserId()))
                .andExpect(jsonPath("$.sender.userId").value(user2.getUserId()));
    }

    @Test
    public void message_test11_Given_SenderIdNotExists_When_createMessage_Then_ResourceNotFoundException() throws Exception {
        when(messageService.createMessage(anyLong(), anyLong(), any())).thenThrow(ResourceNotFoundException.class);

        MessageNewDto newDto = new MessageNewDto("new message");

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .header("X-Current-User-Id", 3)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test12_Given_recipientIdNotExists_When_createMessage_Then_ResourceNotFoundException() throws Exception {
        when(messageService.createMessage(anyLong(), anyLong(), any())).thenThrow(ResourceNotFoundException.class);

        MessageNewDto newDto = new MessageNewDto("new message");

        mockMvc.perform(post("/api/v1/private/messages/users/3")
                        .header("X-Current-User-Id", 2)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test13_Given_recipientIdEqualsSenderId_When_createMessage_Then_ActionForbiddenException()
            throws Exception {

        when(messageService.createMessage(anyLong(), anyLong(), any())).thenThrow(ActionForbiddenException.class);

        MessageNewDto newDto = new MessageNewDto("new message");

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void message_test14_GetAllSentMessagesTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        when(messageService.findAllSentMessages(anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/private/messages/sent")
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].messageId").value(1))
                .andExpect(jsonPath("$.[0].message").value("new message"))
                .andExpect(jsonPath("$.[0].sender.userId").value(2))
                .andExpect(jsonPath("$.[0].recipient.userId").value(1));
    }

    @Test
    public void message_test15_Given_SenderIdNotExists_When_findAllSentMessages_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findAllSentMessages(anyLong())).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/sent")
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test16_GetAllReceivedMessagesTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        when(messageService.findAllReceivedMessages(anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/private/messages/received")
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].messageId").value(1))
                .andExpect(jsonPath("$.[0].message").value("new message"))
                .andExpect(jsonPath("$.[0].sender.userId").value(2))
                .andExpect(jsonPath("$.[0].recipient.userId").value(1));
    }

    @Test
    public void message_test17_Given_SenderIdNotExists_When_findAllReceivedMessages_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findAllReceivedMessages(anyLong())).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/received")
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test18_GetMessageByIdTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        when(messageService.findMessageById(1L, 2L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/private/messages/1")
                        .header("X-Current-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(1))
                .andExpect(jsonPath("$.message").value("new message"))
                .andExpect(jsonPath("$.sender.userId").value(2))
                .andExpect(jsonPath("$.recipient.userId").value(1));
    }

    @Test
    public void message_test19_Given_SenderIdNotExists_When_GetMessageById_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findMessageById(1L, 3L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/1")
                        .header("X-Current-User-Id", 3)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test20_Given_MessageIdNotExists_When_GetMessageById_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findMessageById(3L, 1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/3")
                        .header("X-Current-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test21_Given_CurrentUserIsNotSenderOrReceiver_When_GetMessageById_Then_ActionForbiddenException()
            throws Exception {
        when(messageService.findMessageById(anyLong(), anyLong())).thenThrow(ActionForbiddenException.class);
        mockMvc.perform(get("/api/v1/private/messages/1")
                        .header("X-Current-User-Id", 3)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void message_test22_deleteMessageTest() throws Exception {
        doNothing().when(messageService).deleteMessage(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/private/messages/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void message_test23_deleteMessageTestThrowsActionForbiddenException() throws Exception {
        doThrow(ActionForbiddenException.class).when(messageService).deleteMessage(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/private/messages/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    public void message_test24_deleteMessageTestThrowsResourceNotFoundException() throws Exception {
        doThrow(ResourceNotFoundException.class).when(messageService).deleteMessage(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/private/messages/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void message_test25_Given_MessageIsNull_When_createMessageTest_Then_BadRequest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);

        MessageNewDto newDto = new MessageNewDto(null);

        when(messageService.createMessage(anyLong(), anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .header("X-Current-User-Id", 2)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Message cannot be blank")));
    }

    @Test
    public void message_test26_Given_MessageLength540Chars_When_createMessageTest_Then_BadRequest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);

        MessageNewDto newDto = new MessageNewDto(
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");

        when(messageService.createMessage(anyLong(), anyLong(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .header("X-Current-User-Id", 2)
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Message length should be 500 chars max")));
    }
}
