package dev.practice.blogproject.dtos;

import dev.practice.blogproject.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long messageId;
    private String message;
    private User sender;
    private User recipient;
    private LocalDateTime created = LocalDateTime.now();

}
