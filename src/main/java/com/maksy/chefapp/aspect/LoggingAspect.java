package com.maksy.chefapp.aspect;

import com.maksy.chefapp.service.EmailService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private EmailService emailService;

    @Pointcut("execution(* com.maksy.chefapp.service..*(..)) || execution(* com.maksy.chefapp.repository..*(..))")
    public void serviceAndRepositoryPointCut() {}


    // Логування перед методом
    @Before("serviceAndRepositoryPointCut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering method: {}.{}() with arguments = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    // Логування після успішного виконання
    @AfterReturning(pointcut = "serviceAndRepositoryPointCut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method: {}.{}() with result = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    // Логування та надсилання на email після помилки
    @AfterThrowing(pointcut = "serviceAndRepositoryPointCut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method: {}.{}() with cause = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage(), exception);

        String subject = "Error in Application: " + joinPoint.getSignature().getName();
        String message = "Error details:\n" + exception.getMessage();
        emailService.sendErrorNotification(subject, message);
    }
}