package com.example.vishnu.Nectar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(unique = true, nullable = false)
    private String uniqueIdentifier;
    private String requestEndPoint;
    private String requestType;
    @Column(columnDefinition = "LONGTEXT")
    private String responsePayload;
    private String userName;
    private String time;
}