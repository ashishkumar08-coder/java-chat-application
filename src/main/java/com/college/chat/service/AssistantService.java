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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AssistantService {

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // 🛑 IMPORTANT: Ensure there are no leading/trailing spaces in this string
    private final String API_KEY = System.getenv("GROQ_API_KEY"); 

    public String processAIRequest(String userPrompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Fix: Strict formatting for the Authorization header
            headers.set("Authorization", "Bearer " + API_KEY.trim());
            
            // Fix: OpenRouter REQUIRES these for free models to prevent 401/403 errors
            headers.set("HTTP-Referer", "https://java-chat-application-wrof.onrender.com");
            headers.set("X-Title", "College Chat Application");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "google/gemini-2.0-flash-lite-preview-02-05:free");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", userPrompt));
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List choices = (List) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    return message.get("content").toString();
                }
            }
            return "🤖 AI Response was empty. Please try again.";

        } catch (HttpClientErrorException.Unauthorized e) {
            return "🤖 AI Error 401: API Key rejected. Please verify your key on OpenRouter.ai";
        } catch (Exception e) {
            return "🤖 AI Error: " + e.getMessage();
        }
    }

    public String translateText(String content) {
        String prompt = "Translate the following text to English. If it is already English, translate it to Spanish. Return ONLY the translated text: " + content;
        return processAIRequest(prompt);
    }
}
