package dev.practice.blogproject.controllers;

import dev.practice.blogproject.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TagController {

    private final TagService tagService;
}
