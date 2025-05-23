package com.maksy.chefapp.aspect;

import com.maksy.chefapp.service.EmailService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.Mockito.*;

class LoggingAspectTest {

    private LoggingAspect loggingAspect;
    private EmailService emailService;
    private JoinPoint joinPoint;
    private Signature signature;

    @BeforeEach
    void setUp() throws Exception {
        loggingAspect = new LoggingAspect();
        emailService = mock(EmailService.class);

        Field emailServiceField = LoggingAspect.class.getDeclaredField("emailService");
        emailServiceField.setAccessible(true);
        emailServiceField.set(loggingAspect, emailService);

        joinPoint = mock(JoinPoint.class);
        signature = mock(Signature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.maksy.chefapp.service.SomeService");
        when(signature.getName()).thenReturn("doSomething");
        when(joinPoint.getArgs()).thenReturn(new Object[] { "testArg" });
    }

    @Test
    void testLogAfterThrowing_sendsEmailNotification() {
        Exception exception = new RuntimeException("Test error");

        loggingAspect.logAfterThrowing(joinPoint, exception);

        verify(emailService, times(1)).sendErrorNotification(
                eq("Error in Application: doSomething"),
                eq("Error details:\nTest error")
        );
    }

    @Test
    void testLogBefore_executesWithoutException() {
        assertThatNoException().isThrownBy(() -> loggingAspect.logBefore(joinPoint));
    }

    @Test
    void testLogAfterReturning_executesWithoutException() {
        Object result = "Success";
        assertThatNoException().isThrownBy(() -> loggingAspect.logAfterReturning(joinPoint, result));
    }

    @Test
    void testPointcutMethod_CanBeCalled() {
        assertThatNoException().isThrownBy(() -> loggingAspect.serviceAndRepositoryPointCut());
    }
}
