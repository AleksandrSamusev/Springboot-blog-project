package dev.practice.mainApp.controllers._admin;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin/users")
@AllArgsConstructor
public class UserAdminController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/ban")
    public ResponseEntity<UserFullDto> banUser(@PathVariable Long userId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.banUser(userId, userDetails.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/unban")
    public ResponseEntity<UserFullDto> unbanUser(@PathVariable Long userId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.unbanUser(userId, userDetails.getUsername()), HttpStatus.OK);
    }
}
