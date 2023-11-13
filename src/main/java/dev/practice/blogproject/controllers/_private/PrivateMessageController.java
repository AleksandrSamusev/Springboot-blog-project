package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.message.MessageNewDto;
import dev.practice.blogproject.services.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private/messages/")
public class PrivateMessageController {
    private final MessageService messageService;

    @PostMapping("users/{recipientId}")
    public ResponseEntity<MessageFullDto> createMessage(@PathVariable Long recipientId,
                                                        @RequestHeader("X-Current-User-Id") Long currentUserId,
                                                        @Valid @RequestBody MessageNewDto dto) {
        return new ResponseEntity<>(messageService
                .createMessage(recipientId, currentUserId, dto), HttpStatus.CREATED);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageFullDto>> getAllSentMessages(
            @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(messageService.findAllSentMessages(currentUserId), HttpStatus.OK);
    }

    @GetMapping("/received")
    public ResponseEntity<List<MessageFullDto>> getAllReceivedMessages(
            @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(messageService.findAllReceivedMessages(currentUserId), HttpStatus.OK);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageFullDto> getMessageById(@PathVariable Long messageId,
                                                         @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(messageService.findMessageById(messageId, currentUserId), HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<HttpStatus> deleteMessage(@PathVariable Long messageId,
                                                    @RequestHeader("X-Current-User-Id") Long currentUserId) {
        messageService.deleteMessage(messageId, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
