package dev.practice.mainApp.dtos.message;

import dev.practice.mainApp.dtos.user.UserShortDto;
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

    @NotNull(message = "Message ID cannot be null")
    private Long messageId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotNull(message = "Sender cannot be null")
    private UserShortDto sender;

    @NotNull(message = "Recipient cannot be null")
    private UserShortDto recipient;

    @NotNull(message = "Created parameter cannot be null")
    private LocalDateTime created;

    @NotNull(message = "Is deleted cannot be null")
    private boolean isDeleted;

}
