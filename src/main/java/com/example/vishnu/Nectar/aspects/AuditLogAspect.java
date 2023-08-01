package com.example.vishnu.Nectar.aspects;

import com.example.vishnu.Nectar.entity.AuditLog;
import com.example.vishnu.Nectar.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.UUID;

@Aspect
@Component
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ThreadLocal<String> uniqueIdentifierThreadLocal = new ThreadLocal<>();

    @Autowired
    public AuditLogAspect(AuditLogRepository auditLogRepository){
        this.auditLogRepository = auditLogRepository;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    private void getMapping(){}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    private void postMapping(){}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    private void putMapping(){}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    private void deleteMapping(){}

    @Pointcut("getMapping() || postMapping() || putMapping() || deleteMapping()")
    private void allHttpMethods() {}

    @Before("allHttpMethods()")
    private void logBeforeRequestMapping(JoinPoint jointPoint){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestEndPoint = request.getRequestURI();
        String requestType = request.getMethod();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String uniqueIdentifier = generateUniqueIdentifier();
        uniqueIdentifierThreadLocal.set(uniqueIdentifier);
        AuditLog auditLog = new AuditLog();
        auditLog.setUniqueIdentifier(uniqueIdentifier);
        auditLog.setRequestEndPoint(requestEndPoint);
        auditLog.setRequestType(requestType);
        auditLog.setUserName(username);
        auditLog.setTime(String.valueOf(LocalDateTime.now()));
        auditLogRepository.save(auditLog);
    }

    @AfterReturning(pointcut = "allHttpMethods()", returning = "response")
    public void afterRequestMapping(JoinPoint joinPoint, Object response){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String endpoint = request.getRequestURI();
        String uniqueIdentifier = uniqueIdentifierThreadLocal.get();
        AuditLog auditLog = auditLogRepository.findByUniqueIdentifier(uniqueIdentifier);
        if (auditLog != null) {
            String responsePayload = (response != null) ? response.toString() : "No response payload";
            auditLog.setResponsePayload(responsePayload);
            auditLogRepository.save(auditLog);
        }
    }

    private String generateUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }
}