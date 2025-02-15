package com.itzkoictu.gotNow.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;

@Setter

@Getter@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    @Lob
    private Blob image;


    private String downloadUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
