package dev.practice.blogproject.dtos.message;

import dev.practice.blogproject.dtos.user.UserShortDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageShortDto {

    private Long messageId;

    @NotBlank
    private String message;

    @NotNull
    private UserShortDto sender;

    @NotNull
    private UserShortDto recipient;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private Boolean isDeleted;
}
