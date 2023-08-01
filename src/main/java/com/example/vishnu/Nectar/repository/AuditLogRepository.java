package com.example.vishnu.Nectar.repository;

import com.example.vishnu.Nectar.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    AuditLog findByUniqueIdentifier(String identifier);
}