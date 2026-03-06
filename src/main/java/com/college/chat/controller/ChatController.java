package com.college.chat.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.college.chat.model.ChatMessage;
import com.college.chat.service.AssistantService;
import com.college.chat.service.EncryptionService;

@Controller
public class ChatController {

    @Autowired private AssistantService assistant;
    @Autowired private EncryptionService encryption;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        String content = chatMessage.getContent().trim();
        String lowerContent = content.toLowerCase();

        // 1. Logic for Real AI Assistant
        if (lowerContent.startsWith("@bot")) {
            String prompt = content.substring(4).trim();
            String aiResponse = assistant.processAIRequest(prompt);
            
            chatMessage.setContent(aiResponse);
            chatMessage.setSender("AI_ASSISTANT"); // Set specialized sender
        } 
        
        // 2. Logic for AI Translator
        else if (lowerContent.startsWith("translate")) {
            String translated = assistant.translateText(content);
            
            chatMessage.setContent(translated);
            chatMessage.setSender("AI_TRANSLATOR"); // Set specialized sender
        }

        // Professional Encryption Log (Visible in Admin Panel)
        String encryptedLog = encryption.encrypt(chatMessage.getContent());
        System.out.println("[SECURITY VAULT]: " + encryptedLog);

        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        return chatMessage;
    }
}