//package com.itzkoictu.gotNow.service.product;
//
//import com.itzkoictu.gotNow.dto.request.AddProductRequest;
//import com.itzkoictu.gotNow.dto.request.ProductUpdateRequest;
//import com.itzkoictu.gotNow.dto.response.ProductResponse;
//import com.itzkoictu.gotNow.model.Product;
//
//import java.util.List;
//
//public interface IProductService {
//    Product addProduct(AddProductRequest product);
//    Product updateProduct(ProductUpdateRequest product, Long productId);
//    Product getProductById(Long productId);
//    void deleteProductById(Long productId);
//
//    List<Product> getAllProducts();
//    List<Product> getProductsByCategoryAndBrand(String category, String brand);
//    List<Product> getProductsByCategory(String category);
//    List<Product> getProductsByBrandAndName(String brand, String name);
//    List<Product> getProductsByBrand(String brand);
//    List<Product> getProductsByName(String name);
//
//
//    List<ProductResponse> getConvertedProducts(List<Product> products);
//
//    ProductResponse convertToProductResponse(Product product);
//}
