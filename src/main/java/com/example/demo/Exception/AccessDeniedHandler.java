package com.example.demo.Exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AccessDeniedHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public final void handleDemoException(AccessDeniedException ex) {
        System.out.println("handle DemoException...");
        System.out.println("error message:" + ex.getMessage());
        System.out.println("error cause:" + ex.getCause());
    }
}
