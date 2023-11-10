package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.MessageFullDto;
import dev.practice.blogproject.models.Message;

import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {

    public static Message toMessage(MessageFullDto dto) {
        return new Message(
                dto.getMessageId(),
                dto.getMessage(),
                dto.getSender(),
                dto.getRecipient(),
                dto.getCreated()
        );
    }

    public static MessageFullDto toMessageDto(Message message) {
        return new MessageFullDto(
                message.getMessageId(),
                message.getMessage(),
                message.getSender(),
                message.getRecipient(),
                message.getCreated()
        );
    }

    public static List<Message> toMessages(List<MessageFullDto> dtos) {
        return dtos.stream().map(MessageMapper::toMessage).collect(Collectors.toList());
    }

    public static List<MessageFullDto> toMessageDtos(List<Message> messages) {
        return messages.stream().map(MessageMapper::toMessageDto).collect(Collectors.toList());
    }
}
