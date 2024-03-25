package com.cloud.queriosity.controller;


import com.cloud.queriosity.entity.Message;
import com.cloud.queriosity.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
public class LoginController {

    private final MessageService messageService;


    @Autowired
    public LoginController(MessageService messageService) {
        this.messageService = messageService;
    }

    //Serve Landing Main Page
    @GetMapping("/index")
    public String home() {
        return "index";
    }

    @GetMapping("/success")
    public String loginSuccess() {
        return "main";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "File upload failed: No file selected.";
        }
        return "redirect:/chat";
    }

    @GetMapping("/chat")
    public String chatPage(HttpSession session, Model model) {
        // Generate a new UUID if it doesn't exist and store it in the session
        String userUuid = (String) session.getAttribute("userUuid");
        if (userUuid == null) {
            userUuid = UUID.randomUUID().toString();
            session.setAttribute("userUuid", userUuid);
        }
        // Add the UUID to the model so Thymeleaf can access it
        List<Message> chatHistory = messageService.findRecentMessagesByUserUuid(userUuid);
        Collections.reverse(chatHistory); // Reverse the list in the controller
        model.addAttribute("chatHistory", chatHistory);
        model.addAttribute("userUuid", userUuid);
        return "chat";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(Authentication authentication) {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String username = authToken.getPrincipal().getAttribute("username");
        return "Welcome, " + username;
    }


    @PostMapping("/send-message")
    public ResponseEntity<?> receiveMessage(@RequestBody Message message, HttpSession session) {


        String userUuid = (String) session.getAttribute("userUuid");
        if (userUuid == null || userUuid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User UUID not found in session.");
        }
        Message savedMessage = messageService.saveMessage(userUuid, message.getContent());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Message received successfully.");
        response.put("userUuid", userUuid);
        response.put("content", savedMessage.getContent());
        response.put("timestamp", savedMessage.getTimestamp());
        return ResponseEntity.ok(response);
    }


}