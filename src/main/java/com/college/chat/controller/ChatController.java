package com.college.chat.controller;

import com.college.chat.model.ChatMessage;
import com.college.chat.service.AssistantService;
import com.college.chat.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @Autowired
    private AssistantService assistantService;

    @Autowired
    private EncryptionService encryptionService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        String content = chatMessage.getContent();

        // AI Assistant logic
        if (content.startsWith("@bot")) {
            String aiResponse = assistantService.processAIRequest(content.replace("@bot", ""));
            chatMessage.setContent("🤖 Assistant: " + aiResponse);
        } 
        
        // Translation logic
        else if (content.toLowerCase().contains("translate to")) {
            chatMessage.setContent(assistantService.translateText(content, "Target Language"));
        }

        // Professional Log for Admin Dashboard
        System.out.println("[ADMIN LOG] Encrypted: " + encryptionService.encrypt(chatMessage.getContent()));
        
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