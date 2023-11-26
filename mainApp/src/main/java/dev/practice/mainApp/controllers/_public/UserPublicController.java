package dev.practice.mainApp.controllers._public;

import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
