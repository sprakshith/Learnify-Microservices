package com.rsp.learnify.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Mono;

public class WebClientServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private WebClientService webClientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetJWT_Success() throws Exception {
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");

        String token = webClientService.getJWT();

        assertEquals("test-token", token);
    }

    @Test
    public void testGetJWT_Failure() {
        when(httpRequest.getHeader("Authorization")).thenReturn(null);

        assertThrows(Exception.class, () -> {
            webClientService.getJWT();
        });
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testSendNotification_Success() throws Exception {
        // Setup mocks
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("key", "value");
        String uri = "http://example.com";

        WebClient mockWebClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec mockRequestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(mockWebClient);
        when(mockWebClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri(uri)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.header(anyString(), anyString())).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.bodyValue(requestBody)).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(Mono.empty());
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");

        // Test
        webClientService.sendNotification(requestBody, uri);

        // Verify interactions
        verify(webClientBuilder).build();
        verify(mockWebClient).post();
        verify(mockRequestBodyUriSpec).uri(uri);
        verify(mockRequestBodyUriSpec).header("Authorization", "Bearer test-token");
        verify(mockRequestBodyUriSpec).bodyValue(requestBody);
        verify(mockRequestHeadersSpec).retrieve();
        verify(mockResponseSpec).toBodilessEntity();
    }
}
