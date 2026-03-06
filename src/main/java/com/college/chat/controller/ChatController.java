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

    @Autowired
    private AssistantService assistant;

    @Autowired
    private EncryptionService encryption;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getContent() == null) return chatMessage;
        
        String content = chatMessage.getContent().trim();
        String lowerContent = content.toLowerCase();

        // Detect AI Trigger
        if (lowerContent.startsWith("@bot")) {
            String userQuery = content.substring(4).trim();
            chatMessage.setContent(assistant.processAIRequest(userQuery));
            chatMessage.setSender("SYSTEM_AI"); 
        } 
        // Detect Translation Trigger
        else if (lowerContent.startsWith("translate")) {
            chatMessage.setContent(assistant.translateText(content));
            chatMessage.setSender("SYSTEM_TRANSLATOR");
        }

        // Always log encrypted version to server console for Admin tracking
        System.out.println("[ADMIN VAULT LOG]: " + encryption.encrypt(chatMessage.getContent()));

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