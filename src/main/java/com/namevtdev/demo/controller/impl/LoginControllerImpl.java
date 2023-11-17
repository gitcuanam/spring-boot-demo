package com.namevtdev.demo.controller.impl;

import com.namevtdev.demo.controller.LoginController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginControllerImpl implements LoginController {
    @Override
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        String message = "Hello, Spring Boot!";
        return ResponseEntity.ok(message);
    }
}
