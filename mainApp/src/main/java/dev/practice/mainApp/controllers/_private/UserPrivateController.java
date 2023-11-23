package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class UserPrivateController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserFullDto>> getAllUsers(
            @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(userService.getAllUsers(currentUserId), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserFullDto> getUserById(@PathVariable Long userId,
                                                   @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(userService.getUserById(userId, currentUserId), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userId,
                                                 @RequestHeader("X-Current-User-Id") Long currentUserId) {
        userService.deleteUser(userId, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
