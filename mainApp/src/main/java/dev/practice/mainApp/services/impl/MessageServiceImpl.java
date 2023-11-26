package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
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

@Service
@Slf4j
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Validations validations;

    @Override
    public MessageFullDto createMessage(Long recipientId, String currentUsername, MessageNewDto dto) {
        User recipient = validations.checkUserExist(recipientId);
        User sender = userRepository.findByUsername(currentUsername);
        validations.checkSenderIsNotRecipient(recipient.getUserId(), sender.getUserId());

        Message message = MessageMapper.toMessage(dto, recipient, sender);

        Message savedMessage = messageRepository.save(message);
        sender.getSentMessages().add(savedMessage);
        userRepository.save(sender);
        recipient.getReceivedMessages().add(savedMessage);
        userRepository.save(recipient);
        log.info("Message with ID = " + savedMessage.getMessageId() +
                " was sent by user with ID = " + sender.getUserId() +
                " to user with ID = " + recipientId);
        return MessageMapper.toMessageFullDto(savedMessage);
    }

    @Override
    public List<MessageFullDto> findAllSentMessages(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername);
        log.info("Returned all sent messages of user with ID = " + currentUser.getUserId());
        return MessageMapper.toListMessageFull(currentUser.getSentMessages().stream().toList());
    }

    @Override
    public List<MessageFullDto> findAllReceivedMessages(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername);
        List<Message> filteredMessage = currentUser.getReceivedMessages()
                .stream()
                .filter(message -> !message.getIsDeleted())
                .toList();

        log.info("Returned all received messages of user with ID = " + currentUser.getUserId());
        return MessageMapper.toListMessageFull(filteredMessage);
    }

    @Override
    public MessageFullDto findMessageById(Long messageId, String currentUsername) {
        User user = validations.checkUserExistsByUsernameOrEmail(currentUsername);
        Message message = validations.checkIfMessageExists(messageId);
        if (!message.getSender().getUsername().equals(currentUsername) ||
                !message.getRecipient().getUsername().equals(currentUsername)) {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        } else {
            log.info("Return message with ID = " + messageId + " to user with username = " + currentUsername);
            return MessageMapper.toMessageFullDto(message);
        }
    }

    @Override
    public void deleteMessage(Long messageId, String currentUsername) {
        Message message = validations.checkIfMessageExists(messageId);
        User user = validations.checkUserExistsByUsernameOrEmail(currentUsername);
        if (message.getRecipient().getUsername().equals(currentUsername)) {
            message.setIsDeleted(Boolean.TRUE);
            messageRepository.save(message);
            log.info("Message with ID = " + messageId + " marked as deleted by user with username = "
                    + currentUsername);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }
}