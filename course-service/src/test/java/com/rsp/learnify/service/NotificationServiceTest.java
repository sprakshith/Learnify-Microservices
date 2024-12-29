package com.rsp.learnify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private WebClientService webClientService;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Map<String, String>> requestBodyCaptor;

    @Test
    void courseCreation_ShouldSendNotification() throws Exception {
        // Given
        String teacherId = "1";
        String courseId = "course123";
        String courseTitle = "Java Programming";
        String expectedUri = "http://notification-service:8080/api/v1/notifications/courses/created";

        // When
        notificationService.courseCreation(teacherId, courseId, courseTitle);

        // Then
        verify(webClientService).sendNotification(requestBodyCaptor.capture(), eq(expectedUri));

        Map<String, String> capturedBody = requestBodyCaptor.getValue();
        assertEquals(teacherId, capturedBody.get("from"));
        assertEquals("Enrolled Students", capturedBody.get("to"));
        assertEquals("NEW COURSE", capturedBody.get("type"));
        assertEquals(courseId, capturedBody.get("courseId"));
        assertEquals(
                "I am happy to announce that I have launched a new course with the title 'Java Programming'. Check out soon!",
                capturedBody.get("message"));
    }

    @Test
    void courseUpdate_ShouldSendNotification() throws Exception {
        // Given
        String teacherId = "1";
        String courseId = "course123";
        String courseTitle = "Java Programming";
        String expectedUri = "http://notification-service:8080/api/v1/notifications/courses/updated";

        // When
        notificationService.courseUpdate(teacherId, courseId, courseTitle);

        // Then
        verify(webClientService).sendNotification(requestBodyCaptor.capture(), eq(expectedUri));

        Map<String, String> capturedBody = requestBodyCaptor.getValue();
        assertEquals(teacherId, capturedBody.get("from"));
        assertEquals("Enrolled Students", capturedBody.get("to"));
        assertEquals("COURSE UPDATE", capturedBody.get("type"));
        assertEquals(courseId, capturedBody.get("courseId"));
        assertEquals("I have updated the course with the title 'Java Programming'. Check out the changes!",
                capturedBody.get("message"));
    }

    @Test
    void courseCreation_WhenWebClientFails_ShouldThrowException() throws Exception {
        // Given
        doThrow(new Exception("Network error"))
                .when(webClientService)
                .sendNotification(any(), any());

        // When/Then
        assertThrows(Exception.class, () -> notificationService.courseCreation("1", "course123", "Java"));
    }

    @Test
    void courseUpdate_WhenWebClientFails_ShouldThrowException() throws Exception {
        // Given
        doThrow(new Exception("Network error"))
                .when(webClientService)
                .sendNotification(any(), any());

        // When/Then
        assertThrows(Exception.class, () -> notificationService.courseUpdate("1", "course123", "Java"));
    }
}