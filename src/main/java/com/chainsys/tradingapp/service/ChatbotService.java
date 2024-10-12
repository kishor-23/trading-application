package com.chainsys.tradingapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ChatbotService() {
        this.restTemplate = new RestTemplate();
    }

    public String getChatbotResponse(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> messagePart = new HashMap<>();
        messagePart.put("text", userMessage);

        Map<String, Object> contentsPart = new HashMap<>();
        contentsPart.put("parts", new Object[]{messagePart});

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", new Object[]{contentsPart});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = apiUrl + "/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            return "An error occurred while processing your request.";
        }
    }

    public String processMessageAndImage(String message, MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        // Prepare the request body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("message", message);
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Update URL to the correct endpoint
        String url = apiUrl + "/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            return "An error occurred while processing your request: " + e.getMessage();
        }
    }
}
