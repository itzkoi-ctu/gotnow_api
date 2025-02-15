package com.itzkoictu.gotNow.controller;

import com.itzkoictu.gotNow.dto.response.ApiResponse;
import com.itzkoictu.gotNow.dto.response.ImageResponse;
import com.itzkoictu.gotNow.dto.response.ResponseError;
import com.itzkoictu.gotNow.model.Image;
import com.itzkoictu.gotNow.service.image.ImageService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> uploadImage(
                                    @RequestParam("files") List<MultipartFile> files,
                                    @RequestParam("productId") Long productId){
        try {
            List<ImageResponse> imageResponses= imageService.saveImage(productId, files);
            return new ApiResponse<>(HttpStatus.OK.value(), "Images uploaded successfully", imageResponses);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Image upload error: "+ e.getMessage());
        }
    }

    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId){
        try {
            Image image= imageService.getImageById(imageId);
            ByteArrayResource resource= new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+ image.getFileName()+"\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().header("image not found").build();
        }
    }

    @PutMapping("/image/update/{imageId}")
    public  ApiResponse<?> updateImage(
            @RequestParam("file") MultipartFile image ,
            @PathVariable Long imageId){
        try {
            imageService.updateImage(image, imageId);
            return new ApiResponse<>(HttpStatus.OK.value(), "image updated successfully");
        } catch (EntityNotFoundException e) {
            return new ResponseError(HttpStatus.CONFLICT.value(), "Error: "+ e.getMessage());
        }
    }
    @DeleteMapping("/image/delete/{imageId}")
    public  ApiResponse<?> deleteImage(@PathVariable Long imageId){
        try {
            imageService.deleteImageById( imageId);
            return new ApiResponse<>(HttpStatus.OK.value(), "image deleted successfully");
        } catch (EntityExistsException e) {
            return new ResponseError(HttpStatus.CONFLICT.value(), "Error: "+ e.getMessage());
        }
    }
}
