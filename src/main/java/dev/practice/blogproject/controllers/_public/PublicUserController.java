package dev.practice.blogproject.controllers._public;

import dev.practice.blogproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicUserController {

    private final UserService userService;

}
