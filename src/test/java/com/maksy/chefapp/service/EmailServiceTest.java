package com.maksy.chefapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

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
        String expectedMessage = "Test message";

        // Act
        emailService.sendErrorNotification(expectedSubject, expectedMessage);

        // Assert
        verify(mailSender).send(argThat((SimpleMailMessage mail) ->
                Objects.requireNonNull(mail.getTo())[0].equals(testRecipient) &&
                        Objects.equals(mail.getSubject(), expectedSubject) &&
                        Objects.equals(mail.getText(), expectedMessage)
        ));
    }
}