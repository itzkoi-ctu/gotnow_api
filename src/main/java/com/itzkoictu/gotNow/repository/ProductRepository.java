package com.itzkoictu.gotNow.repository;

import com.itzkoictu.gotNow.model.Category;
import com.itzkoictu.gotNow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryAndBrand(Category category, String brand);

    List<Product> findByCategory(Category category);

    List<Product> findByBrandAndName(String brand, String name);

    List<Product> findByBrand(String brand);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Product> findByName(String name);

    boolean existsByNameAndBrand(String name, String brand);

    List<Product> findAllByCategoryId(Long categoryId);
}
