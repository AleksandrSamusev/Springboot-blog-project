package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.message.MessageFullDto;
import dev.practice.mainApp.dtos.message.MessageNewDto;
import dev.practice.mainApp.services.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private/messages/")
public class MessagePrivateController {
    private final MessageService messageService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("users/{recipientId}")
    public ResponseEntity<MessageFullDto> createMessage(@AuthenticationPrincipal UserDetails currentUser,
                                                        @PathVariable Long recipientId,
                                                        @Valid @RequestBody MessageNewDto dto) {
        return new ResponseEntity<>(messageService
                .createMessage(recipientId, currentUser.getUsername(), dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/sent")
    public ResponseEntity<List<MessageFullDto>> getAllSentMessages(
            @AuthenticationPrincipal UserDetails currentUser) {
        return new ResponseEntity<>(messageService.findAllSentMessages(currentUser.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/received")
    public ResponseEntity<List<MessageFullDto>> getAllReceivedMessages(
            @AuthenticationPrincipal UserDetails currentUser) {
        return new ResponseEntity<>(messageService.findAllReceivedMessages(currentUser.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageFullDto> getMessageById(@AuthenticationPrincipal UserDetails currentUser,
                                                         @PathVariable Long messageId) {
        return new ResponseEntity<>(messageService.findMessageById(
                messageId, currentUser.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<HttpStatus> deleteMessage(@AuthenticationPrincipal UserDetails currentUser,
                                                    @PathVariable Long messageId) {
        messageService.deleteMessage(messageId, currentUser.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
