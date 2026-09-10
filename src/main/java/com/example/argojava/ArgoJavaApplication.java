package com.example.argojava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;
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
    private final RestClient userServiceClient;

    HelloController(
            @Value("${APP_MESSAGE:Hello from Java deployed by Argo CD!}") String message,
            @Value("${USER_SERVICE_URL:http://localhost:8080}") String userServiceUrl) {
        this.message = message;
        this.userServiceClient = RestClient.builder().baseUrl(userServiceUrl).build();
    }

    @GetMapping("/api/hello")
    String hello() {
        return message;
    }

    @GetMapping("/api/users")
    String users() {
        return "Users from user-service";
    }

    @GetMapping("/api/orders")
    String orders() {
        String users = userServiceClient.get().uri("/api/users").retrieve().body(String.class);
        return "Orders from order-service; dependency response: " + users;
    }
}
