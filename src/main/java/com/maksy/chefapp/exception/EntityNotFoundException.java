package com.maksy.chefapp.exception;

public class EntityNotFoundException extends RuntimeException {
    // Код статусу, який описує тип помилки
    private final String statusCode;

    // Конструктор, який приймає статус і повідомлення про помилку
    public EntityNotFoundException(String statusCode, String message) {
        super(message);                                         // Викликає конструктор RuntimeException з повідомленням
        this.statusCode = statusCode;
    }

    // Геттер для отримання статус-коду
    public String getStatusCode() {
        return statusCode;
    }
}