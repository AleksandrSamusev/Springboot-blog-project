package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;

import java.util.List;

public interface MessageService {
    MessageFullDto createMessage(Long recipientId, String currentUsername, MessageNewDto dto);

    List<MessageFullDto> findAllSentMessages(String currentUsername);

    List<MessageFullDto> findAllReceivedMessages(String currentUsername);

    MessageFullDto findMessageById(Long messageId, String currentUsername);

    void deleteMessage(Long messageId, String currentUsername);

}
