package com.example.spbn3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class ReplicateService {

    @Value("${replicate.api.token}")
    private String apiToken; // 👈 ĐẶT Ở ĐÂY

    private final RestTemplate restTemplate = new RestTemplate();

    public String callAI(String prompt) {

        String url = "https://api.replicate.com/v1/models/stability-ai/sdxl/predictions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Token " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "input": {
                    "prompt": "%s"
                  }
                }
                """.formatted(prompt);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }
}