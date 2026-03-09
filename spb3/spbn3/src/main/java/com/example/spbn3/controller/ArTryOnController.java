package com.example.spbn3.controller;

import com.example.spbn3.service.ArTryOnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ar")
public class ArTryOnController {

    @Autowired
    private ArTryOnService arTryOnService;

    @PostMapping("/try-on")
    public ResponseEntity<Map<String, String>> tryOn(
            @RequestParam("userImage") MultipartFile userImage,
            @RequestParam("productImage") MultipartFile productImage,
            @RequestParam(value = "category", defaultValue = "upper_body") String category) {

        Map<String, String> response = new HashMap<>();

        if (userImage.isEmpty() || productImage.isEmpty()) {
            response.put("error", "Vui lòng cung cấp đủ hình ảnh người dùng và hình ảnh sản phẩm.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String resultImageUrl = arTryOnService.generateTryOnImage(userImage, productImage, category);

            if (resultImageUrl != null) {
                response.put("success", "true");
                response.put("resultUrl", resultImageUrl);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Có lỗi xảy ra trong quá trình tạo ảnh AR.");
                return ResponseEntity.internalServerError().body(response);
            }

        } catch (Exception e) {
            String errMsg = e.getMessage();
            if (errMsg != null && errMsg.contains("ERROR_API_KEY_MISSING")) {
                response.put("error", "ERROR_API_KEY_MISSING");
                return ResponseEntity.status(401).body(response); // Unauthorized
            }
            response.put("error", "Lỗi server: " + errMsg);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
