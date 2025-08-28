package com.HuangYe.WildNest.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * AOP统一日志切面
 * 记录Controller层的请求和响应信息
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 定义切点：Controller层的所有方法
     */
    @Pointcut("execution(public * com.HuangYe.WildNest.controller..*.*(..))")
    public void controllerLog() {}
    
    /**
     * 定义切点：Service层的所有方法
     */
    @Pointcut("execution(public * com.HuangYe.WildNest.service..*.*(..))")
    public void serviceLog() {}
    
    /**
     * 前置通知：记录请求信息
     */
    @Before("controllerLog()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // 记录请求信息
                log.info("========== 请求开始 ==========");
                log.info("请求URL: {}", request.getRequestURL().toString());
                log.info("请求方法: {}", request.getMethod());
                log.info("请求IP: {}", getClientIpAddress(request));
                log.info("请求类方法: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
                log.info("请求参数: {}", Arrays.toString(joinPoint.getArgs()));
            }
        } catch (Exception e) {
            log.error("记录请求日志异常", e);
        }
    }
    
    /**
     * 环绕通知：记录方法执行时间和返回结果
     */
    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        
        try {
            result = proceedingJoinPoint.proceed();
            return result;
        } finally {
            try {
                long endTime = System.currentTimeMillis();
                log.info("执行时间: {} ms", endTime - startTime);
                log.info("返回结果: {}", objectMapper.writeValueAsString(result));
                log.info("========== 请求结束 ==========\n");
            } catch (Exception e) {
                log.error("记录响应日志异常", e);
            }
        }
    }
    
    /**
     * 异常通知：记录异常信息
     */
    @AfterThrowing(pointcut = "controllerLog()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        try {
            log.error("========== 请求异常 ==========");
            log.error("异常方法: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            log.error("异常信息: ", exception);
            log.error("========== 异常结束 ==========\n");
        } catch (Exception e) {
            log.error("记录异常日志失败", e);
        }
    }
    
    /**
     * Service层方法执行时间记录
     */
    @Around("serviceLog()")
    public Object doServiceAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = proceedingJoinPoint.getSignature().getDeclaringTypeName() + "." + proceedingJoinPoint.getSignature().getName();
        
        try {
            Object result = proceedingJoinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // 只记录执行时间超过100ms的Service方法
            long executionTime = endTime - startTime;
            if (executionTime > 100) {
                log.info("Service方法 {} 执行时间: {} ms", methodName, executionTime);
            }
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("Service方法 {} 执行异常，执行时间: {} ms", methodName, endTime - startTime, e);
            throw e;
        }
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}