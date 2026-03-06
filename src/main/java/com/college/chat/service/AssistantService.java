package com.college.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AssistantService {

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // 🛑 PASTE YOUR KEY HERE (Ensure no extra spaces inside the quotes)
    private final String API_KEY = "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"; 

    public String processAIRequest(String userPrompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Fix for 401: Added clear space after Bearer
            headers.set("Authorization", "Bearer " + API_KEY);
            
            // Fix for 401: Required headers for OpenRouter Free Models
            headers.set("HTTP-Referer", "https://universal-chat-app.onrender.com");
            headers.set("X-Title", "College Chat Application");
            headers.set("User-Agent", "Mozilla/5.0"); 

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "google/gemini-2.0-flash-lite-preview-02-05:free");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", userPrompt));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Making the actual call to the AI Cloud
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List choices = (List) response.getBody().get("choices");
                Map firstChoice = (Map) choices.get(0);
                Map message = (Map) firstChoice.get("message");
                return message.get("content").toString();
            } else {
                return "🤖 AI Error: Server returned " + response.getStatusCode();
            }

        } catch (Exception e) {
            return "🤖 AI Offline: " + e.getMessage();
        }
    }

    public String translateText(String content) {
        String prompt = "Translate the following text accurately. Return ONLY the translated text: " + content;
        return processAIRequest(prompt);
    }
}