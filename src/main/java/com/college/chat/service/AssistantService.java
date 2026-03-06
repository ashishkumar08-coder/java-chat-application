package com.college.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssistantService {

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String processAIRequest(String userPrompt) {
        try {
            // This pulls the key from the Cloud Environment safely
            String apiKey = System.getenv("OPENROUTER_API_KEY");
            
            if (apiKey == null || apiKey.isEmpty()) {
                return "🤖 AI Error: API Key not configured in Cloud Environment.";
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://localhost:8080"); // Required by OpenRouter

            Map<String, Object> requestBody = new HashMap<>();
            // Using a high-quality free model
            requestBody.put("model", "google/gemini-2.0-flash-lite-preview-02-05:free");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", userPrompt));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            List choices = (List) response.getBody().get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");
            
            return message.get("content").toString();

        } catch (Exception e) {
            return "🤖 AI is thinking... (Error: " + e.getMessage() + ")";
        }
    }

    public String translateText(String content) {
        return processAIRequest("Translate this message accurately. Return ONLY the translated text: " + content);
    }
}