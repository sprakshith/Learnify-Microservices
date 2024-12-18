package com.rsp.learnify.notificationservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rsp.learnify.notificationservice.dto.NotificationRequest;
import com.rsp.learnify.notificationservice.service.KafkaProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/courses/general-announcement")
    public void sendGeneralAnnouncementNotifications(@RequestBody NotificationRequest notificationRequest) {
        try {
            kafkaProducerService.generalAnnouncementNotification(notificationRequest);
        } catch (Exception e) {
            log.error("Error sending general announcement notification: {}", e.getMessage());
        }
    }

    @PostMapping("/courses/created")
    public void sendCourseCreatedNotifications(@RequestBody NotificationRequest notificationRequest) {
        try {
            kafkaProducerService.newCourseCreatedNotification(notificationRequest);
        } catch (Exception e) {
            log.error("Error sending course created notification: {}", e.getMessage());
        }
    }

    @PostMapping("/courses/updated")
    public void sendCourseUpdatedNotifications(@RequestBody NotificationRequest notificationRequest) {
        try {
            kafkaProducerService.courseUpdatedNotification(notificationRequest);
        } catch (Exception e) {
            log.error("Error sending course updated notification: {}", e.getMessage());
        }
    }

    @PostMapping("/courses/enrolled")
    public void sendCourseEnrolledNotifications(@RequestBody NotificationRequest notificationRequest) {
        try {
            kafkaProducerService.enrolmentNotification(notificationRequest);
        } catch (Exception e) {
            log.error("Error sending general announcement notification: {}", e.getMessage());
        }
    }

}
