package dev.practice.blogproject.controllers;

import dev.practice.blogproject.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MessageController {
    private final MessageService messageService;
}
