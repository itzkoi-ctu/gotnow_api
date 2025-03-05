package com.itzkoictu.gotNow.model;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")  // Tên collection trong MongoDB
public class Rating {
    @Id
    private String id;
    private Long orderId;
    private Long productId;  // Liên kết với MySQL
    private Long userId;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;


}

