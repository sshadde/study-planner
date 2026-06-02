package ru.studyplanner.mediator;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
