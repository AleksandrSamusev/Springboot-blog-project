package dev.practice.blogproject.controllers._private;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserUpdateDto;
import dev.practice.blogproject.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class PrivateUserController {
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

    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserFullDto> updateUser(@PathVariable Long userId,
                                                  @RequestHeader("X-Current-User-Id") Long currentUserId,
                                                  @Valid @RequestBody UserUpdateDto dto) {
        return new ResponseEntity<>(userService.updateUser(userId, currentUserId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long userId,
                                                 @RequestHeader("X-Current-User-Id") Long currentUserId) {
        userService.deleteUser(userId, currentUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
