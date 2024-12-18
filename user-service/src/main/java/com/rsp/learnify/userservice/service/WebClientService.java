package com.rsp.learnify.userservice.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebClientService {
    private final WebClient.Builder webClientBuilder;

    private final HttpServletRequest httpRequest;

    public String getJWT() throws Exception {
        try {
            return httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }
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
