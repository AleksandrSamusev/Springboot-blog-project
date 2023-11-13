package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.message.MessageNewDto;
import dev.practice.blogproject.models.Message;
import dev.practice.blogproject.models.User;

import java.time.LocalDateTime;

public class MessageMapper {

    public static MessageFullDto toMessageFullDto(Message message) {
        return new MessageFullDto(
                message.getMessageId(),
                message.getMessage(),
                UserMapper.toUserShortDto(message.getSender()),
                UserMapper.toUserShortDto(message.getRecipient()),
                message.getCreated(),
                message.getIsDeleted()
        );
    }

    public static Message toMessage(MessageNewDto dto, User recipient, User currentUser) {
        Message message = new Message();
        message.setMessage(dto.getMessage());
        message.setCreated(LocalDateTime.now());
        message.setIsDeleted(Boolean.FALSE);
        message.setSender(currentUser);
        message.setRecipient(recipient);
        return message;
    }
}
