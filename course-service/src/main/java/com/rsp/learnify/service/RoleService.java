package com.rsp.learnify.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final WebClient.Builder webClientBuilder;

    private final HttpServletRequest httpRequest;

    private boolean hasRole(String role) throws Exception {

        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/is-" + role)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public boolean isAdmin() throws Exception {
        return hasRole("admin");
    }

    public boolean isTeacher() throws Exception {
        return hasRole("teacher");
    }

    public boolean isStudent() throws Exception {
        return hasRole("student");
    }

}
