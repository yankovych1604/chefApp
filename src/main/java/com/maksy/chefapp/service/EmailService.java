package com.maksy.chefapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    @Value("${error.mail.to}")
    String to;

    public void sendErrorNotification(String subject, String message) {
        StringBuilder emailBody = new StringBuilder();

        // Додаємо часову мітку в форматі "yyyy-MM-dd HH:mm:ss"
        String timestamp = java.time.LocalDateTime.now().toString().replace("T", " ");
        emailBody.append(timestamp).append(" ERROR ").append(" - ").append(subject).append("\n");

        // Основне повідомлення про помилку
        emailBody.append(message).append("\n\n");

        // Стек викликів — бекрейс (імітація), щоб краще бачити, де сталася помилка
        emailBody.append("Бекрейс:\n");

        Exception e = new Exception(message);
        for (StackTraceElement element : e.getStackTrace()) {
            emailBody.append(element.toString()).append("\n");
        }

        // Створення email-повідомлення
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);                                                                         // Кому надіслати
        mailMessage.setSubject(subject);                                                                   // Тема листа
        mailMessage.setText(emailBody.toString());                                                        // Текст листа
        mailSender.send(mailMessage);                                                                      // Надсилання
    }
}