package com.cloud.queriosity.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userUuid;
    private String content;

    private LocalDateTime timestamp = LocalDateTime.now(); // Automatically set the timestamp when a message is created

    public Message(String userUuid, String content) {
        this.userUuid = userUuid;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Sets the timestamp when a message is instantiated
    }
}