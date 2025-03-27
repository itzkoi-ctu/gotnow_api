package com.itzkoictu.gotNow.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "messages")

public class Message {
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime timestamp;
}
