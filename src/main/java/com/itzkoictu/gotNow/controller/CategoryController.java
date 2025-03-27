package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.ResponseError;
import com.itzkoictu.gotNow.model.Category;

import com.itzkoictu.gotNow.service.category.CategoryService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ApiResponse<?> getAllCategories(){

        try {
            List<Category> categories= categoryService.getAllCategories();
            return new ApiResponse(HttpStatus.OK.value(), "get all category success" ,categories);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error: "+ e.getMessage());
        }
    }

    @PostMapping("/add")
    public ApiResponse<?> addCategory(@RequestBody Category category){
        try {
            Category theCategory= categoryService.addCategory(category);
            return new ApiResponse(HttpStatus.OK.value(), "create category success", theCategory);
        } catch (EntityExistsException e) {
            return new ResponseError(HttpStatus.CONFLICT.value(), "Error: "+ e.getMessage());
        }
    }

    @GetMapping("/category/{id}")
    public ApiResponse<Category> getCategoryById(@PathVariable Long id){
        try {
            Category theCategory= categoryService.findCategoryById(id);
            return new ApiResponse(HttpStatus.OK.value(), "create category success", theCategory);
        } catch (EntityNotFoundException e) {
            return new ResponseError(HttpStatus.NOT_FOUND.value(), "Error: "+ e.getMessage());
        }
    }
    @PutMapping("/category/update/{categoryId}")
    public ApiResponse<Category> updateCategory(@RequestBody Category category, @PathVariable Long categoryId){
        try {
            Category theCategory= categoryService.updateCategory(category, categoryId);
            return new ApiResponse(HttpStatus.ACCEPTED.value(), "update category success", theCategory);
        } catch (EntityNotFoundException e) {
            return new ResponseError(HttpStatus.NOT_FOUND.value(), "Error: "+ e.getMessage());
        }
    }

    @DeleteMapping("/category/delete/{categoryId}")
    public ApiResponse<Category> deleteCategory( @PathVariable Long categoryId){
        try {
            categoryService.deleteCategory( categoryId);
            return new ApiResponse(HttpStatus.ACCEPTED.value(), "delete category success");
        } catch (EntityNotFoundException e) {
            return new ResponseError(HttpStatus.NOT_FOUND.value(), "Error: "+ e.getMessage());
        }
    }

    @GetMapping("/category/by/{categoryName}")
    public ApiResponse<Category> getCategoryByName(@PathVariable String categoryName){
        try {
            Category theCategory= categoryService.findCategoryByName(categoryName);
            return new ApiResponse(HttpStatus.OK.value(), "get category success", theCategory);
        } catch (EntityNotFoundException e) {
            return new ResponseError(HttpStatus.NOT_FOUND.value(), "Error: "+ e.getMessage());
        }
    }



}
