package com.itzkoictu.gotNow.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itzkoictu.gotNow.dto.request.CategoryRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter

@Getter@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public Category(String name) {
        this.name = name;
    }

    public CategoryRequest toCategoryRequest(String category){
        CategoryRequest categoryRequest= new CategoryRequest(category);
        return  categoryRequest;
    }



}
