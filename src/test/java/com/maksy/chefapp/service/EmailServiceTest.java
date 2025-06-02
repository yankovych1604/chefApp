package com.maksy.chefapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private final String testRecipient = "errors@chefapp.com";

    @Test
    void sendErrorNotification_ShouldSendEmailWithCorrectParameters() {
        // Arrange
        emailService.to = testRecipient; // Direct field injection for testing
        String subject = "Test Error";
        String message = "Something went wrong";

        // Act
        emailService.sendErrorNotification(subject, message);

        // Assert
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendErrorNotification_ShouldUseConfiguredRecipient() {
        // Arrange
        emailService.to = testRecipient;
        String expectedSubject = "Test Error";
        String expectedMessagePart = "Test message";

        // Act
        emailService.sendErrorNotification(expectedSubject, expectedMessagePart);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage actualMessage = captor.getValue();

        assertEquals(testRecipient, Objects.requireNonNull(actualMessage.getTo())[0]);
        assertEquals(expectedSubject, actualMessage.getSubject());
        // Перевіряємо, що основне повідомлення входить у текст (ігноруємо дату і бекрейс)
        assertTrue(actualMessage.getText().contains(expectedMessagePart));
        // Додатково — можна перевірити, що у тексті є слово "Бекрейс"
        assertTrue(actualMessage.getText().contains("Бекрейс:"));
    }
}