package com.itzkoictu.gotNow.service.image;

import com.itzkoictu.gotNow.dto.response.ImageResponse;
import com.itzkoictu.gotNow.model.Image;
import com.itzkoictu.gotNow.model.Product;
import com.itzkoictu.gotNow.repository.ImageRepository;
import com.itzkoictu.gotNow.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ImageService  {
    ImageRepository imageRepository;
    ProductService productService;

    public Image getImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("image not found"));
    }

    public void deleteImageById(Long imageId) {
        imageRepository.findById(imageId).ifPresentOrElse(imageRepository::delete,
                () -> {
                    throw new EntityNotFoundException("image not found");
                });
    }

    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ImageResponse> saveImage(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);

        List<ImageResponse> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/api/v1/images/image/download/";
                String downloadUrl = buildDownloadUrl + image.getId();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRepository.save(image);
                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
                imageRepository.save(savedImage);

                ImageResponse imageResponse = new ImageResponse();
                imageResponse.setId(savedImage.getId());
                imageResponse.setFileName(savedImage.getFileName());
                imageResponse.setDownloadUrl(savedImage.getDownloadUrl());
                savedImages.add(imageResponse);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return savedImages;
    }
}
