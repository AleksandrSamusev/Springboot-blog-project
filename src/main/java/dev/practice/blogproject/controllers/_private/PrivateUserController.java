package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class PrivateUserController {
    private final UserService userService;
}
