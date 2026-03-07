package com.college.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class AssistantService {

    private final String API_KEY = System.getenv("GROQ_API_KEY");
    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getAIResponse(String userPrompt) {
        return callGroq(userPrompt);
    }

    // NEW: Method specifically for translation
    public String translateText(String text, String targetLanguage) {
        String prompt = "Translate the following text to " + targetLanguage + ". Only return the translated text, nothing else: " + text;
        return callGroq(prompt);
    }

    private String callGroq(String prompt) {
        if (API_KEY == null || API_KEY.isEmpty()) return "AI Error: Key missing.";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            return "AI Error: " + e.getMessage();
        }
    }
}
