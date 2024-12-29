package com.rsp.learnify.notificationservice.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.rsp.learnify.notificationservice.dto.NotificationRequest;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Test
    void enrolmentNotification_ShouldSendMessageToCorrectTopic() {
        NotificationRequest request = NotificationRequest.builder()
                .message("Test enrolment message")
                .build();

        kafkaProducerService.enrolmentNotification(request);

        verify(kafkaTemplate).send("enrolment-notification", "Test enrolment message");
    }

    @Test
    void newCourseCreatedNotification_ShouldSendMessageToCorrectTopic() {
        NotificationRequest request = NotificationRequest.builder()
                .message("Test course creation message")
                .build();

        kafkaProducerService.newCourseCreatedNotification(request);

        verify(kafkaTemplate).send("course-created-notification", "Test course creation message");
    }

    @Test
    void courseUpdatedNotification_ShouldSendMessageToCorrectTopic() {
        NotificationRequest request = NotificationRequest.builder()
                .message("Test course update message")
                .build();

        kafkaProducerService.courseUpdatedNotification(request);

        verify(kafkaTemplate).send("course-updated-notification", "Test course update message");
    }

    @Test
    void courseDeletedNotification_ShouldSendMessageToCorrectTopic() {
        NotificationRequest request = NotificationRequest.builder()
                .message("Test course deletion message")
                .build();

        kafkaProducerService.courseDeletedNotification(request);

        verify(kafkaTemplate).send("course-deleted-notification", "Test course deletion message");
    }

    @Test
    void generalAnnouncementNotification_ShouldSendMessageToCorrectTopic() {
        NotificationRequest request = NotificationRequest.builder()
                .message("Test announcement message")
                .build();

        kafkaProducerService.generalAnnouncementNotification(request);

        verify(kafkaTemplate).send("general-announcement-notification", "Test announcement message");
    }
}