package com.itzkoictu.gotNow.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {
    String name;

    public CategoryRequest(String name) {
        this.name = name;
    }
}
