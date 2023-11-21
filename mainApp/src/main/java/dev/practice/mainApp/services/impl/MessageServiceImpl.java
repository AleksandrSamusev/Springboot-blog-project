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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageFullDto createMessage(Long recipientId, Long currentUserId, MessageNewDto dto) {
        checkIfUserExists(recipientId);
        checkIfUserExists(currentUserId);
        checkIfActionAllowed(recipientId, currentUserId);

        User recipient = userRepository.findById(recipientId).get();
        User currentUser = userRepository.findById(currentUserId).get();

        Message message = MessageMapper.toMessage(dto, recipient, currentUser);

        Message savedMessage = messageRepository.save(message);
        currentUser.getSentMessages().add(savedMessage);
        userRepository.save(currentUser);
        recipient.getReceivedMessages().add(savedMessage);
        userRepository.save(recipient);
        log.info("Message with ID = " + savedMessage.getMessageId() +
                " was sent by user with ID = " + currentUserId +
                " to user with ID = " + recipientId);
        return MessageMapper.toMessageFullDto(savedMessage);
    }

    @Override
    public List<MessageFullDto> findAllSentMessages(Long currentUserId) {
        checkIfUserExists(currentUserId);
        log.info("Returned all sent messages of user with ID = " + currentUserId);
        return userRepository.findById(currentUserId).get().getSentMessages().stream().map(
                MessageMapper::toMessageFullDto).toList();
    }

    @Override
    public List<MessageFullDto> findAllReceivedMessages(Long currentUserId) {
        checkIfUserExists(currentUserId);
        log.info("Returned all received messages of user with ID = " + currentUserId);
        return userRepository.findById(currentUserId).get().getReceivedMessages().stream().map(
                MessageMapper::toMessageFullDto).toList();
    }

    @Override
    public MessageFullDto findMessageById(Long messageId, Long currentUserId) {
        checkIfMessageExists(messageId);
        checkIfUserExists(currentUserId);
        Message message = messageRepository.findById(messageId).get();
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
        checkIfUserExists(currentUserId);
        checkIfMessageExists(messageId);
        Message message = messageRepository.findById(messageId).get();
        if (message.getRecipient().getUserId().equals(currentUserId)) {
            message.setIsDeleted(Boolean.TRUE);
            messageRepository.save(message);
            log.info("Message with ID = " + messageId + " marked as deleted by user with ID = " + currentUserId);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    private void checkIfMessageExists(Long id) {
        if (!messageRepository.existsById(id)) {
            log.info("ResourceNotFoundException. Message with given ID = " + id + " not found");
            throw new ResourceNotFoundException("Message with given ID = " + id + " not found");
        }
    }

    private void checkIfUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            log.info("ResourceNotFoundException. User with given ID = " + id + " not found");
            throw new ResourceNotFoundException("User with given ID = " + id + " not found");
        }
    }

    private void checkIfActionAllowed(Long recipientId, Long currentUserId) {
        if (recipientId.equals(currentUserId)) {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }
}