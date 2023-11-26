package dev.practice.mainApp.controllers._private;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/private")
public class UserPrivateController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/users")
    public ResponseEntity<List<UserFullDto>> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.getAllUsers(userDetails.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserFullDto> updateUser(@PathVariable Long userId,
                                                  @Valid @RequestBody UserUpdateDto dto,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(userService.updateUser(userId, dto, userDetails.getUsername()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserFullDto> getUserById(@PathVariable Long userId,
                                                   HttpServletRequest request) {
        return new ResponseEntity<>(userService.getUserById(userId, request.getUserPrincipal().getName()),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userId, userDetails.getUsername());
        return ResponseEntity.ok("User with ID = " + userId + " was successfully deleted.");
    }
}