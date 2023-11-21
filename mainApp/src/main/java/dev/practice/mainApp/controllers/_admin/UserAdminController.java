package dev.practice.mainApp.controllers._admin;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.services.UserService;
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

    @PatchMapping("/{userId}")
    public ResponseEntity<UserFullDto> changeRole(@PathVariable Long userId,
                                                  @RequestParam String role) {
        return new ResponseEntity<>(userService.changeRole(userId, role), HttpStatus.OK);
    }
}