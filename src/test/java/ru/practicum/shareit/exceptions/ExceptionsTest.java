package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionsTest {

    @Test
    void notFoundException() {
        Exception exception = new NotFoundException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void conflictException() {
        Exception exception = new ConflictException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void emailException() {
        Exception exception = new EmailException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void validatationException() {
        Exception exception = new ValidationException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }
}
