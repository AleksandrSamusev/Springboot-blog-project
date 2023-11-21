package dev.practice.blogproject.controllers._admin;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/users")
@AllArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @PatchMapping("/{userId}/ban")
    public ResponseEntity<UserFullDto> banUser(@PathVariable Long userId,
                                               @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(userService.banUser(userId, currentUserId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/unban")
    public ResponseEntity<UserFullDto> unbanUser(@PathVariable Long userId,
                                                @RequestHeader("X-Current-User-Id") Long currentUserId) {
        return new ResponseEntity<>(userService.unbanUser(userId, currentUserId), HttpStatus.OK);
    }
}
