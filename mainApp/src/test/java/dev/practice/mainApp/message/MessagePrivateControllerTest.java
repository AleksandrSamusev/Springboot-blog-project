package dev.practice.mainApp.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.config.SecurityConfig;
import dev.practice.mainApp.controllers._private.MessagePrivateController;
import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationEntryPoint;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.impl.MessageServiceImpl;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(MessagePrivateController.class)
public class MessagePrivateControllerTest {
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
    private MessageServiceImpl messageService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserShortDto user1 = new UserShortDto(1L, "username1");
    private final UserShortDto user2 = new UserShortDto(2L, "username2");

    @WithMockUser
    @Test
    public void message_test10_createMessageTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        MessageNewDto newDto = new MessageNewDto("new message");

        when(messageService.createMessage(anyLong(), anyString(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/private/messages/users/1")
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

    @WithMockUser
    @Test
    public void message_test11_Given_SenderIdNotExists_When_createMessage_Then_ResourceNotFoundException() throws Exception {
        when(messageService.createMessage(anyLong(), anyString(), any())).thenThrow(ResourceNotFoundException.class);

        MessageNewDto newDto = new MessageNewDto("new message");

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void message_test12_Given_recipientIdNotExists_When_createMessage_Then_ResourceNotFoundException() throws Exception {
        when(messageService.createMessage(anyLong(), anyString(), any())).thenThrow(ResourceNotFoundException.class);

        MessageNewDto newDto = new MessageNewDto("new message");

        mockMvc.perform(post("/api/v1/private/messages/users/3")
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void message_test13_Given_recipientIdEqualsSenderId_When_createMessage_Then_ActionForbiddenException()
            throws Exception {

        when(messageService.createMessage(anyLong(), anyString(), any())).thenThrow(ActionForbiddenException.class);

        MessageNewDto newDto = new MessageNewDto("new message");

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithMockUser
    @Test
    public void message_test14_GetAllSentMessagesTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        when(messageService.findAllSentMessages(anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/private/messages/sent")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].messageId").value(1))
                .andExpect(jsonPath("$.[0].message").value("new message"))
                .andExpect(jsonPath("$.[0].sender.userId").value(2))
                .andExpect(jsonPath("$.[0].recipient.userId").value(1));
    }

    @WithMockUser
    @Test
    public void message_test15_Given_SenderIdNotExists_When_findAllSentMessages_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findAllSentMessages(anyString())).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/sent")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void message_test16_GetAllReceivedMessagesTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);
        when(messageService.findAllReceivedMessages(anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/private/messages/received")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].messageId").value(1))
                .andExpect(jsonPath("$.[0].message").value("new message"))
                .andExpect(jsonPath("$.[0].sender.userId").value(2))
                .andExpect(jsonPath("$.[0].recipient.userId").value(1));
    }

    @WithMockUser
    @Test
    public void message_test17_Given_SenderIdNotExists_When_findAllReceivedMessages_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findAllReceivedMessages(anyString())).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/received")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(roles = "USER")
    @Test
    public void message_test18_GetMessageByIdTest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);

        when(messageService.findMessageById(anyLong(), any())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/private/messages/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(1))
                .andExpect(jsonPath("$.message").value("new message"))
                .andExpect(jsonPath("$.sender.userId").value(2))
                .andExpect(jsonPath("$.recipient.userId").value(1));
    }

    @WithMockUser
    @Test
    public void message_test19_Given_SenderIdNotExists_When_GetMessageById_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findMessageById(anyLong(), any()))
                .thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void message_test20_Given_MessageIdNotExists_When_GetMessageById_Then_ResourceNotFoundException()
            throws Exception {
        when(messageService.findMessageById(anyLong(), any()))
                .thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get("/api/v1/private/messages/3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void message_test21_Given_CurrentUserIsNotSenderOrReceiver_When_GetMessageById_Then_ActionForbiddenException()
            throws Exception {
        when(messageService.findMessageById(anyLong(), anyString())).thenThrow(ActionForbiddenException.class);
        mockMvc.perform(get("/api/v1/private/messages/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithMockUser
    @Test
    public void message_test22_deleteMessageTest() throws Exception {
        doNothing().when(messageService).deleteMessage(anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/private/messages/1"))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    public void message_test23_deleteMessageTestThrowsActionForbiddenException() throws Exception {
        doThrow(ActionForbiddenException.class).when(messageService).deleteMessage(anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/private/messages/1"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser
    @Test
    public void message_test24_deleteMessageTestThrowsResourceNotFoundException() throws Exception {
        doThrow(ResourceNotFoundException.class).when(messageService).deleteMessage(anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/private/messages/1"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @Test
    public void message_test25_Given_MessageIsNull_When_createMessageTest_Then_BadRequest() throws Exception {

        MessageFullDto dto = new MessageFullDto(
                1L, "new message", user2, user1, LocalDateTime.now(), false);

        MessageNewDto newDto = new MessageNewDto(null);

        when(messageService.createMessage(anyLong(), anyString(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Message cannot be blank")));
    }

    @WithMockUser
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

        when(messageService.createMessage(anyLong(), anyString(), any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/private/messages/users/1")
                        .content(mapper.writeValueAsString(newDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Message length should be 500 chars max")));
    }
}
