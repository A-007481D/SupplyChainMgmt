package com.tricol.manage_supplier_orders.shared.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.tricol.manage_supplier_orders..application.service..*(..)) || execution(* com.tricol.manage_supplier_orders..api.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Entering: " + joinPoint.getSignature());
    }

    @AfterThrowing(value = "execution(* com.tricol.manage_supplier_orders..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        System.err.println("Exception in: " + joinPoint.getSignature() + " -> " + ex.getMessage());
    }
}
