package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
}
