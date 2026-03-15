package com.kolmir.fitness_tracker.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.kolmir.fitness_tracker.controllers..*) || within(com.kolmir.fitness_tracker.services..*)")
    public void applicationLayer() {
    }

    @Around("applicationLayer()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().toShortString();

        if (logger.isInfoEnabled()) {
            logger.info("-> {} args={}", methodName, formatArgs(joinPoint.getArgs()));
        }

        long startedAt = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        if (logger.isDebugEnabled()) {
            logger.debug("<- {} completed in {} ms", methodName, System.currentTimeMillis() - startedAt);
        }

        return result;
    }

    @AfterThrowing(pointcut = "applicationLayer()", throwing = "exception")
    public void logError(JoinPoint joinPoint, Throwable exception) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        logger.error("xx {} failed: {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    private String formatArgs(Object[] args) {
        return Arrays.stream(args)
                .map(this::sanitizeArg)
                .toList()
                .toString();
    }

    private Object sanitizeArg(Object arg) {
        if (arg instanceof MultipartFile file) {
            return "MultipartFile{name='%s', size=%d}".formatted(file.getOriginalFilename(), file.getSize());
        }
        return arg;
    }
}
