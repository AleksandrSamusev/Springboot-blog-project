package dev.practice.mainApp.exceptions;

public class ActionForbiddenException extends RuntimeException {
    public ActionForbiddenException(String message) {
        super(message);
    }
}
