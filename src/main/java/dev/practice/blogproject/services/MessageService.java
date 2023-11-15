package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.message.MessageNewDto;
import org.springframework.http.HttpStatusCode;

import java.util.List;

public interface MessageService {
    MessageFullDto createMessage(Long recipientId, Long currentUserId, MessageNewDto dto);

    List<MessageFullDto> findAllSentMessages(Long currentUserId);

    List<MessageFullDto> findAllReceivedMessages(Long currentUserId);

    MessageFullDto findMessageById(Long messageId, Long currentUserId);

    void deleteMessage(Long messageId, Long currentUserId);

}
