package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.request.AddProductRequest;
import com.itzkoictu.gotNow.dto.request.ProductUpdateRequest;
import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.ProductResponse;
import com.itzkoictu.gotNow.dto.response.ResponseError;
import com.itzkoictu.gotNow.model.Product;
import com.itzkoictu.gotNow.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> productResponses = productService.getConvertedProducts(products);
        return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "all products", productResponses));
    }

    @GetMapping("/product/{productId}/product")
    public ResponseEntity getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            ProductResponse productResponse = productService.convertToProductResponse(product);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "get product success", productResponse));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseError(HttpStatus.NOT_FOUND.value(), "Oops! " + e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity addProduct(@RequestBody AddProductRequest request) {


        Product product = productService.addProduct(request);
        ProductResponse productResponse = productService.convertToProductResponse(product);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(HttpStatus.ACCEPTED.value(), "product added successfully", productResponse));

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity deleteProduct(@PathVariable Long productId) {

        productService.deleteProductById(productId);
        return ResponseEntity.accepted().body(new ApiResponse(HttpStatus.ACCEPTED.value(), "Product deleted successfully!" , productId));

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/product/{productId}/update")
    public ResponseEntity updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest, @PathVariable Long productId) {
        Product product = productService.updateProduct(productUpdateRequest, productId);
        ProductResponse response= productService.convertToProductResponse(product);
        return ResponseEntity.accepted().body(new ApiResponse(HttpStatus.ACCEPTED.value(), "Product updated successfully! ", response));

    }

    @GetMapping("/product/by/brand-and-name")
    public ResponseEntity<ApiResponse> getProductsByBrandAndName(@RequestParam String brand, @RequestParam String productName) {
        try {
            List<Product> products = productService.getProductsByBrandAndName(brand, productName);
            List<ProductResponse> productResponses = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "products by brand and name", productResponses));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("product/by/category-and-brand")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndBrand(@RequestParam String category, @RequestParam String brand) {
        try {
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);
            List<ProductResponse> productResponses = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "products by category and brand", productResponses));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/product/by/category")
    public ResponseEntity<ApiResponse> getProductsByCategory(@RequestParam String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            List<ProductResponse> productResponses = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "products by category", productResponses));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @GetMapping("/category/{categoryId}/products")
    public ResponseEntity<ApiResponse> getProductsByCategoryId(@PathVariable Long categoryId) {
        try {
            List<Product> products = productService.getProductsByCategoryId(categoryId);
            List<ProductResponse> productResponses = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "products by category", productResponses));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/product/by/name")
    public ResponseEntity<ApiResponse> getProductsByName(@RequestParam String productName) {
        try {
            List<Product> products = productService.getProductsByName(productName);
            List<ProductResponse> productResponses = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "products by name", productResponses));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/product/by/brand")
    public ResponseEntity<ApiResponse> getProductsByBrand(@RequestParam String brand) {
        try {
            List<Product> products = productService.getProductsByBrand(brand);
            List<ProductResponse> productResponses = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK.value(), "products by name", productResponses));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/distinct/products")
    public ResponseEntity<ApiResponse> getDistinctProductByName() {
        List<Product> products1 = productService.findDistinctProductByName();
        List<ProductResponse> products = productService.getConvertedProducts(products1);
        return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "All product distinct", products));
    }


    @GetMapping("/distinct/brands")
    public ResponseEntity<ApiResponse> getDistinctBrand() {
        return ResponseEntity.ok().body(new ApiResponse(HttpStatus.OK.value(), "All brands distinct", productService.getAllDistinctBrand()));
    }

}
