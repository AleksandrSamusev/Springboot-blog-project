package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.models.Message;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<MessageFullDto> toMessageDtos(List<Message> messages) {
        return messages.stream().map(MessageMapper::toMessageFullDto).collect(Collectors.toList());
    }
}
