package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.services.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private/messages/")
public class MessagePrivateController {
    private final MessageService messageService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("users/{recipientId}")
    public ResponseEntity<MessageFullDto> createMessage(@PathVariable Long recipientId,
                                                        @RequestHeader("X-Current-User-Id") Long currentUserId,
                                                        @Valid @RequestBody MessageNewDto dto) {
        return new ResponseEntity<>(messageService
                .createMessage(recipientId, currentUserId, dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/sent")
    public ResponseEntity<List<MessageFullDto>> getAllSentMessages(
            @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(messageService.findAllSentMessages(currentUserId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/received")
    public ResponseEntity<List<MessageFullDto>> getAllReceivedMessages(
            @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(messageService.findAllReceivedMessages(currentUserId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageFullDto> getMessageById(@PathVariable Long messageId,
                                                         @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(messageService.findMessageById(messageId, currentUserId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<HttpStatus> deleteMessage(@PathVariable Long messageId,
                                                    @RequestHeader("X-Current-User-Id") Long currentUserId) {
        messageService.deleteMessage(messageId, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
