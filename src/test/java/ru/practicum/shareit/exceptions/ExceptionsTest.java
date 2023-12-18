package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionsTest {

    @Test
    void notFoundExceptionTest() {
        Exception exception = new NotFoundException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void conflictExceptionTest() {
        Exception exception = new ConflictException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void emailExceptionTest() {
        Exception exception = new EmailException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }

    @Test
    void validatationExceptionTest() {
        Exception exception = new ValidationException("Test");
        assertThat(exception.getMessage()).isEqualTo("Test");
    }
}
