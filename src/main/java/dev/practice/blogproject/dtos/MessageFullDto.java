package dev.practice.blogproject.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageFullDto {

    private Long messageId;

    @NotBlank
    private String message;

    @NotNull
    private UserShortDto sender;

    @NotNull
    private UserShortDto recipient;
    private LocalDateTime created = LocalDateTime.now();

    private boolean isDeleted;

}
