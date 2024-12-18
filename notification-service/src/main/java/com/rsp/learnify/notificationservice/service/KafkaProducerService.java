package com.rsp.learnify.notificationservice.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rsp.learnify.notificationservice.dto.NotificationRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void enrolmentNotification(NotificationRequest notificationRequest) {
        sendMessage("enrolment-notification", notificationRequest.getMessage());
    }

    public void newCourseCreatedNotification(NotificationRequest notificationRequest) {
        sendMessage("course-created-notification", notificationRequest.getMessage());
    }

    public void courseUpdatedNotification(NotificationRequest notificationRequest) {
        sendMessage("course-updated-notification", notificationRequest.getMessage());
    }

    public void courseDeletedNotification(NotificationRequest notificationRequest) {
        sendMessage("course-deleted-notification", notificationRequest.getMessage());
    }

    public void generalAnnouncementNotification(NotificationRequest notificationRequest) {
        sendMessage("general-announcement-notification", notificationRequest.getMessage());
    }

}
