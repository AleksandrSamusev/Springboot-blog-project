package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.mappers.MessageMapper;
import dev.practice.mainApp.models.Message;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.MessageRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.MessageService;
import dev.practice.mainApp.utils.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Validations validations;

    @Override
    public MessageFullDto createMessage(Long recipientId, Long currentUserId, MessageNewDto dto) {
        User recipient = validations.checkUserExist(recipientId);
        User sender = validations.checkUserExist(currentUserId);
        validations.checkSenderIsNotRecipient(recipient.getUserId(), sender.getUserId());

        Message message = MessageMapper.toMessage(dto, recipient, sender);

        Message savedMessage = messageRepository.save(message);
        sender.getSentMessages().add(savedMessage);
        userRepository.save(sender);
        recipient.getReceivedMessages().add(savedMessage);
        userRepository.save(recipient);
        log.info("Message with ID = " + savedMessage.getMessageId() +
                " was sent by user with ID = " + currentUserId +
                " to user with ID = " + recipientId);
        return MessageMapper.toMessageFullDto(savedMessage);
    }

    @Override
    public List<MessageFullDto> findAllSentMessages(Long currentUserId) {
        User currentUser = validations.checkUserExist(currentUserId);
        log.info("Returned all sent messages of user with ID = " + currentUserId);
        return currentUser.getSentMessages().stream().map(MessageMapper::toMessageFullDto).toList();
    }

    @Override
    public List<MessageFullDto> findAllReceivedMessages(Long currentUserId) {
        User currentUser = validations.checkUserExist(currentUserId);
        log.info("Returned all received messages of user with ID = " + currentUserId);
        return currentUser.getReceivedMessages().stream().map(MessageMapper::toMessageFullDto).toList();
    }

    @Override
    public MessageFullDto findMessageById(Long messageId, Long currentUserId) {
        Message message = validations.checkIfMessageExists(messageId);
        validations.checkUserExist(currentUserId);

        if (message.getSender().getUserId().equals(currentUserId) ||
                message.getRecipient().getUserId().equals(currentUserId)) {
            log.info("Return message with ID = " + messageId + " to user with ID = " + currentUserId);
            return MessageMapper.toMessageFullDto(message);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    @Override
    public void deleteMessage(Long messageId, Long currentUserId) {
        validations.checkUserExist(currentUserId);
        Message message = validations.checkIfMessageExists(messageId);
        if (message.getRecipient().getUserId().equals(currentUserId)) {
            message.setIsDeleted(Boolean.TRUE);
            messageRepository.save(message);
            log.info("Message with ID = " + messageId + " marked as deleted by user with ID = " + currentUserId);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }
}