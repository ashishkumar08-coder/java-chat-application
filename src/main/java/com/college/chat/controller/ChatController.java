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
        String content = chatMessage.getContent();

        if (content.startsWith("@bot")) {
            chatMessage.setContent(assistant.processAIRequest(content.replace("@bot", "")));
        } else if (content.toLowerCase().startsWith("translate to")) {
            chatMessage.setContent(assistant.translateText(content));
        }

        // Log encrypted version for Admin
        System.out.println("[ADMIN VAULT] " + encryption.encrypt(chatMessage.getContent()));
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