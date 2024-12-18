package com.rsp.learnify.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final WebClientService webClientService;

    public void courseCreation(String teacherId, String courseId, String courseTitle) throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("from", teacherId);
        requestBody.put("to", "Enrolled Students");
        requestBody.put("type", "NEW COURSE");
        requestBody.put("courseId", courseId);
        requestBody.put("message",
                String.format(
                        "I am happy to announce that I have launched a new course with the title '%s'. Check out soon!",
                        courseTitle));

        String postUri = "http://notification-service/api/v1/notifications/courses/created";

        webClientService.sendNotification(requestBody, postUri);
    }

    public void courseUpdate(String teacherId, String courseId, String courseTitle) throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("from", teacherId);
        requestBody.put("to", "Enrolled Students");
        requestBody.put("type", "COURSE UPDATE");
        requestBody.put("courseId", courseId);
        requestBody.put("message",
                String.format("I have updated the course with the title '%s'. Check out the changes!",
                        courseTitle));

        String postUri = "http://notification-service/api/v1/notifications/courses/updated";

        webClientService.sendNotification(requestBody, postUri);
    }

}
