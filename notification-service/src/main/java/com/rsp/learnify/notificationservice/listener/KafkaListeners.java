package com.rsp.learnify.notificationservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KafkaListeners {

    @KafkaListener(topics = "enrolment-notification", groupId = "notification-group")
    public void consumeEnrolmentNotification(String message) {
        // For now logging the message. Later if needed, we can send email or SMS
        // notification.
        log.info("Received enrolment notification: " + message);
    }

    @KafkaListener(topics = "course-created-notification", groupId = "notification-group")
    public void consumeCourseCreatedNotification(String message) {
        // For now logging the message. Later if needed, we can send email or SMS
        // notification.
        log.info("Received course created notification: " + message);
    }

    @KafkaListener(topics = "course-updated-notification", groupId = "notification-group")
    public void consumeCourseUpdatedNotification(String message) {
        // For now logging the message. Later if needed, we can send email or SMS
        // notification.
        log.info("Received course updated notification: " + message);
    }

    @KafkaListener(topics = "general-announcement-notification", groupId = "notification-group")
    public void consumeGeneralAnnouncementNotification(String message) {
        // For now logging the message. Later if needed, we can send email or SMS
        // notification.
        log.info("Received general announcement notification: " + message);
    }

}
