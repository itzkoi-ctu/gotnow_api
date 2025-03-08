package com.itzkoictu.gotNow.model;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "conversations")
@Getter
@Setter
public class Conversation {
    @Id
    private String id;
    private Long adminId =1L;
    private Long userId;
    private List<Message> messages= new ArrayList<>();
}
