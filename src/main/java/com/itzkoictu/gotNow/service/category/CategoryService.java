package com.itzkoictu.gotNow.service.category;

import com.itzkoictu.gotNow.model.Category;
import com.itzkoictu.gotNow.repository.CartItemRepository;
import com.itzkoictu.gotNow.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService  {
     CategoryRepository categoryRepository;


    public Category addCategory(Category request) {
        return Optional.of(request).filter(c -> !categoryRepository.existsByName(c.getName()))
                .map(categoryRepository :: save).orElseThrow(() -> new EntityExistsException("category already exists"));
    }

    public Category updateCategory(Category request, Long categoryId) {
        return Optional.ofNullable(findCategoryById(categoryId)).map(oldCategory -> {
            oldCategory.setName(request.getName());
            return categoryRepository.save(oldCategory);

        }).orElseThrow(() -> new EntityNotFoundException("category not found"));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .ifPresentOrElse(categoryRepository :: delete, () -> {
                    throw new EntityNotFoundException("Category not found");
                });

    }

    public Category findCategoryByName(String name) {
        try {
            return categoryRepository.findByName(name);
        } catch (Exception e) {
            throw new EntityNotFoundException("category not found");
        }
    }

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("category not found"));
    }
}
