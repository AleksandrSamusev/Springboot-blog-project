package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.message.MessageNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.MessageMapper;
import dev.practice.blogproject.models.Message;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.MessageRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageFullDto createMessage(Long recipientId, Long currentUserId, MessageNewDto dto) {
        checkIfParametersValid(recipientId, currentUserId, dto);
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
        return MessageMapper.toMessageFullDto(savedMessage);
    }

    @Override
    public List<MessageFullDto> findAllSentMessages(Long currentUserId) {
        checkIfUserExists(currentUserId);
        return userRepository.findById(currentUserId).get().getSentMessages().stream().map(
                MessageMapper::toMessageFullDto).toList();
    }

    @Override
    public List<MessageFullDto> findAllReceivedMessages(Long currentUserId) {
        checkIfUserExists(currentUserId);
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
            return MessageMapper.toMessageFullDto(message);
        } else {
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
        } else {
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    private void checkIfMessageExists(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Message with gicen ID = " + id + " not found");
        }
    }


    private void checkIfUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with given ID = " + id + " not found");
        }
    }

    private void checkIfParametersValid(Long recipientId, Long currentUserId, MessageNewDto dto) {
        if (recipientId == null || currentUserId == null || dto == null) {
            throw new InvalidParameterException("Invalid parameter");
        }
    }

    private void checkIfActionAllowed(Long recipientId, Long currentUserId) {
        if (recipientId.equals(currentUserId)) {
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }
}
