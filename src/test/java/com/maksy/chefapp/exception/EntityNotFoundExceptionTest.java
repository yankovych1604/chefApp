package com.maksy.chefapp.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityNotFoundExceptionTest {
    @Test
    void testConstructorAndGetters() {
        String statusCode = "ENTITY_NOT_FOUND";
        String message = "Ingredient not found";

        EntityNotFoundException exception = new EntityNotFoundException(statusCode, message);

        assertThat(exception.getStatusCode()).isEqualTo(statusCode);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}