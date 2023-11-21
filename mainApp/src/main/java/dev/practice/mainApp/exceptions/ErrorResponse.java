package dev.practice.mainApp.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String path;
    private List<String> errors = new ArrayList<>();

    public ErrorResponse(List<String> errors) {
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }

    public void addError(String message) {
        this.errors.add(message);
    }

}
