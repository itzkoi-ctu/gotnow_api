package com.itzkoictu.gotNow.service.product;

import com.itzkoictu.gotNow.dto.request.AddProductRequest;
import com.itzkoictu.gotNow.dto.request.ProductUpdateRequest;
import com.itzkoictu.gotNow.dto.response.ImageResponse;
import com.itzkoictu.gotNow.dto.response.ProductResponse;
import com.itzkoictu.gotNow.model.*;
import com.itzkoictu.gotNow.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ProductService {
     ProductRepository productRepository;
     CategoryRepository categoryRepository;

     CartItemRepository cartItemRepository;
     OrderItemRepository orderItemRepository;
     ModelMapper mapper;
     ImageRepository imageRepository;

    public Product addProduct(AddProductRequest request) {
        System.out.println(" request+ "+request);
        if(productExist(request.getName(),request.getBrand())){
            throw new EntityExistsException(request.getName() + " already exists");
        }
//        Category category= Optional.ofNullable(request.getCategory())
//                        .map(category1 -> categoryRepository.findByName(category1.getName()))
//                                .orElseGet(() -> {
//                                    if(request.getCategory() != null && request.getCategory().getName() != null){
//                                        Category newCate= new Category(request.getCategory().getName());
//                                        categoryRepository.save(newCate);
//                                    }
//
//                                    return null;
//                                });
        Category category= Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory= new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);

                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));

    }

    private boolean productExist(String name, String brand){
        return productRepository.existsByNameAndBrand(name, brand);
    }
    private Product createProduct(AddProductRequest request, Category category){
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),

                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    public Product updateProduct(ProductUpdateRequest product, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, product ))
                .map(productRepository :: save).orElseThrow(() -> new EntityNotFoundException("Product not found"));

    }
    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest productUpdateRequest){
        existingProduct.setName(productUpdateRequest.getName());
        existingProduct.setBrand(productUpdateRequest.getBrand());
        existingProduct.setPrice(productUpdateRequest.getPrice());
        existingProduct.setInventory(productUpdateRequest.getInventory());
        Category category= categoryRepository.findByName(productUpdateRequest.getCategory().getName());
        existingProduct.setCategory(category);
        existingProduct.setName(productUpdateRequest.getName());
        return existingProduct;

    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElseThrow(()->new EntityNotFoundException("Product not found"));
    }

    public void deleteProductById(Long productId) {
        productRepository.findById(productId)
                .ifPresentOrElse(product -> {
                    List<CartItem> cartItems= cartItemRepository.findByProductId(productId);
                    cartItems.forEach(cartItem -> {
                        Cart cart= cartItem.getCart();
                        cart.removeItem(cartItem);
                        cartItemRepository.delete(cartItem);
                    });

                    List<OrderItem> orderItems= orderItemRepository.findProductById(productId);
                    orderItems.forEach(orderItem -> {
                        orderItem.setProduct(null);
                        orderItemRepository.save(orderItem);
                    });

                    Optional.ofNullable(product.getCategory())
                            .ifPresent(category -> category.getProducts().remove(product));
                            product.setCategory(null);
                            productRepository.deleteById(productId);

                            }, ()->{ throw new EntityNotFoundException("product not found");
                });



    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        Category category1= categoryRepository.findByName(category);
        return productRepository.findByCategoryAndBrand(category1, brand);
    }

    public List<Product> getProductsByCategory(String category) {
        Category category1= categoryRepository.findByName(category);
        return productRepository.findByCategory(category1);
    }

    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    public List<ProductResponse> getConvertedProducts(List<Product> products){
        return products.stream().map(this::convertToProductResponse).toList();
    }

    public ProductResponse convertToProductResponse(Product product){
        ProductResponse productResponse= mapper.map(product, ProductResponse.class);
        List<Image> images= imageRepository.findByProductId(product.getId());
        List<ImageResponse> imageResponses= images.stream().map(image -> mapper.map(image, ImageResponse.class)).toList();
        productResponse.setImages(imageResponses);
        return productResponse;
    }
}
