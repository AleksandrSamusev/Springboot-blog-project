package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.repositories.MessageRepository;
import dev.practice.blogproject.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
}
