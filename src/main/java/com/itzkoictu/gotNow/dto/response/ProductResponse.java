package com.itzkoictu.gotNow.dto.response;

import com.itzkoictu.gotNow.model.Category;
import com.itzkoictu.gotNow.model.Image;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
    private List<ImageResponse> images;



}
