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
public class WebClientService {
    private final WebClient.Builder webClientBuilder;

    private final HttpServletRequest httpRequest;

    private String getJWT() throws Exception {
        try {
            return httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }
    }

    public boolean userHasRole(String role) throws Exception {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/is-" + role)
                .header("Authorization", "Bearer " + getJWT())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    public Map<String, Object> getUserDetails() throws Exception {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/get-user-details")
                .header("Authorization", "Bearer " + getJWT())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }

    public List<String> getEnrolledCourses() throws Exception {
        return webClientBuilder.build()
                .get()
                .uri("http://user-service/api/v1/users/get-enrolled-courses")
                .header("Authorization", "Bearer " + getJWT())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();
    }

    public void sendNotification(Map<String, String> requestBody, String uri) throws Exception {
        webClientBuilder.build()
                .post()
                .uri(uri)
                .header("Authorization", "Bearer " + getJWT())
                .bodyValue(requestBody)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }

}
