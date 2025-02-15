package com.itzkoictu.gotNow.dto.request;

import com.itzkoictu.gotNow.model.Category;

import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequest {

    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;




}
