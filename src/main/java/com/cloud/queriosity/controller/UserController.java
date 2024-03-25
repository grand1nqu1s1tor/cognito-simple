package com.cloud.queriosity.controller;

import com.cloud.queriosity.entity.User;
import com.cloud.queriosity.exception.ResourceNotFoundException;
import com.cloud.queriosity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", principal.getAttribute("username")); // Adjust the attribute name as needed
        return userInfo;

    }

}
