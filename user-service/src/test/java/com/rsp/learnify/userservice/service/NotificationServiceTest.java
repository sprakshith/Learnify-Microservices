package com.rsp.learnify.userservice.service;

import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NotificationServiceTest {

    @Mock
    private WebClientService webClientService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnrolmentNotification() throws Exception {
        String fullName = "John Doe";
        String courseId = "course-1";
        String courseTitle = "Course Title";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("from", "Course Instructor");
        requestBody.put("to", fullName);
        requestBody.put("type", "ENROLMENT");
        requestBody.put("courseId", courseId);
        requestBody.put("message",
                String.format("You have successfully enrolled in the course with the title: %s.", courseTitle));

        String postUri = "http://notification-service:8080/api/v1/notifications/courses/enrolled";

        notificationService.enrolmentNotification(fullName, courseId, courseTitle);

        verify(webClientService).sendNotification(requestBody, postUri);
    }
}
