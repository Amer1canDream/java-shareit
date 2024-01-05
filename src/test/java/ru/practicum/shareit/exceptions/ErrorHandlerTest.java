package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {
    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleConflictExceptionTest() {
        ConflictException conflictException = new ConflictException("test");

        var exception = errorHandler.handleConflictException(conflictException);

        assertEquals(conflictException.getMessage(), exception.getError());
    }

    @Test
    void handleNotFoundTest() {
        NotFoundException conflictException = new NotFoundException("test");

        var exception = errorHandler.handleNotFound(conflictException);

        assertEquals(conflictException.getMessage(), exception.getError());
    }

    @Test
    void handleBadRequestTest() {
        ValidationException conflictException = new ValidationException("test");

        var exception = errorHandler.handleBadRequest(conflictException);

        assertEquals(conflictException.getMessage(), exception.getError());
    }

    @Test
    void handleTest() {
        EmailException conflictException = new EmailException("test");

        var exception = errorHandler.handle(conflictException);

        assertEquals(conflictException.getMessage(), exception.getError());
    }

    @Test
    void handleInternalServerErrorTest() {
        EmailException conflictException = new EmailException("test");

        var exception = errorHandler.handleInternalServerError(conflictException);

        assertEquals(conflictException.getMessage(), exception.getError());
    }
}
