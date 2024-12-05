package com.rsp.learnify.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

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

    public Map<String, Object> getUserDetails() throws Exception {
        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/get-user-details")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    public List<String> getEnrolledCourses() throws Exception {
        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/get-enrolled-courses")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
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
