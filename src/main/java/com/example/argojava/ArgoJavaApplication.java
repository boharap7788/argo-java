package com.example.argojava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ArgoJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArgoJavaApplication.class, args);
    }
}

@RestController
class HelloController {
    private final String message;

    HelloController(@Value("${APP_MESSAGE:Hello from Java deployed by Argo CD!}") String message) {
        this.message = message;
    }

    @GetMapping("/api/hello")
    String hello() {
        return message;
    }
}
