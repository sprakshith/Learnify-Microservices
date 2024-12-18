package com.rsp.learnify.userservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebClientService webClientService;

    public void enrolmentNotification(String fullName, String courseId, String courseTitle) throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("from", "Course Instructor");
        requestBody.put("to", fullName);
        requestBody.put("type", "ENROLMENT");
        requestBody.put("courseId", courseId);
        requestBody.put("message",
                String.format("You have successfully enrolled in the course with the title: %s.", courseTitle));

        String postUri = "http://notification-service/api/v1/notifications/courses/enrolled";

        webClientService.sendNotification(requestBody, postUri);
    }

}
