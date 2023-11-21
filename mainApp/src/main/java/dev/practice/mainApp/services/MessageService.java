package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;

import java.util.List;

public interface MessageService {
    MessageFullDto createMessage(Long recipientId, Long currentUserId, MessageNewDto dto);

    List<MessageFullDto> findAllSentMessages(Long currentUserId);

    List<MessageFullDto> findAllReceivedMessages(Long currentUserId);

    MessageFullDto findMessageById(Long messageId, Long currentUserId);

    void deleteMessage(Long messageId, Long currentUserId);

}
