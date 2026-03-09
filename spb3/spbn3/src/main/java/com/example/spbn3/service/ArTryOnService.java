package com.example.spbn3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ArTryOnService {

    @Value("${replicate.api.token}")
    private String replicateApiToken;

    public String generateTryOnImage(MultipartFile userImage, MultipartFile productImage, String category) {

        if (replicateApiToken == null || replicateApiToken.isEmpty()) {
            throw new RuntimeException("ERROR_API_KEY_MISSING");
        }

        try {

            // ===== 1. Convert ảnh sang Data URI =====
            String userBase64 = Base64.getEncoder().encodeToString(userImage.getBytes());
            String userDataUri = "data:" + userImage.getContentType() + ";base64," + userBase64;

            String productBase64 = Base64.getEncoder().encodeToString(productImage.getBytes());
            String productDataUri = "data:" + productImage.getContentType() + ";base64," + productBase64;

            // ===== 2. Chuẩn bị request =====
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + replicateApiToken);

            Map<String, Object> input = new HashMap<>();
            input.put("human_img", userDataUri);
            input.put("garm_img", productDataUri); // also known as garment_img in some models, wait - I should keep
                                                   // garm_img and add garment_img to be safe
            input.put("garment_img", productDataUri);
            input.put("garment_des", "high quality realistic clothing item");
            input.put("category", category);
            input.put("steps", 30); // best quality for IDM-VTON
            input.put("crop", false);

            Map<String, Object> requestBody = new HashMap<>();
            // Models on replicate are called via their specific version
            requestBody.put("version", "0513734a452173b8173e907e3a59d19a36266e55b48528559432bd21c7d7e985"); // cuuupid/idm-vton
                                                                                                            // specific
                                                                                                            // version
            requestBody.put("input", input);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ===== 3. Gọi API theo cách mới =====
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.replicate.com/v1/predictions",
                    entity,
                    Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Không tạo được prediction");
            }

            Map<String, Object> respMap = response.getBody();
            Map<String, String> urls = (Map<String, String>) respMap.get("urls");
            String predictionUrl = urls.get("get");

            // ===== 4. Polling chờ kết quả =====
            int maxRetries = 25;

            while (maxRetries-- > 0) {

                Thread.sleep(3000);

                HttpHeaders getHeaders = new HttpHeaders();
                getHeaders.set("Authorization", "Bearer " + replicateApiToken);

                HttpEntity<String> getEntity = new HttpEntity<>(getHeaders);

                ResponseEntity<Map> statusResponse = restTemplate.exchange(
                        predictionUrl,
                        HttpMethod.GET,
                        getEntity,
                        Map.class);

                Map<String, Object> statusMap = statusResponse.getBody();
                String status = (String) statusMap.get("status");

                if ("succeeded".equals(status)) {

                    Object output = statusMap.get("output");

                    if (output instanceof String) {
                        return (String) output;
                    }

                    if (output instanceof java.util.List list && !list.isEmpty()) {
                        return list.get(0).toString();
                    }

                    throw new RuntimeException("Không đọc được output từ AI");
                }

                if ("failed".equals(status) || "canceled".equals(status)) {
                    throw new RuntimeException("AI xử lý thất bại");
                }
            }

            throw new RuntimeException("Quá thời gian chờ AI xử lý");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.PAYMENT_REQUIRED) {
                throw new RuntimeException(
                        "Tài khoản Replicate API của bạn đã hết tiền (Insufficient credit). Vui lòng nạp thêm tiền tại replicate.com/account/billing để tiếp tục sử dụng tính năng thử đồ bằng AI.");
            }
            throw new RuntimeException("Lỗi máy chủ khi gọi AI (" + e.getStatusCode() + "): " + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi máy chủ khi gọi AI: " + e.getMessage());
        }
    }
}