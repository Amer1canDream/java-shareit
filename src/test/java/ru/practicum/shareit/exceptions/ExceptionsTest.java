package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionsTest {

    @Test
    void NotFoundException() {
        Exception exception = new NotFoundException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void ConflictException() {
        Exception exception = new ConflictException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void EmailException() {
        Exception exception = new EmailException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void ValidatationException() {
        Exception exception = new ValidationException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }
}
