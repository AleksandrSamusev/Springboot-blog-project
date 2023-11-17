package dev.practice.blogproject.controllers._public;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/public")
public class UserPublicController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserShortDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserShortDto> getUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserFullDto> createUser(@Valid @RequestBody UserNewDto dto) {
        return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
    }

}
