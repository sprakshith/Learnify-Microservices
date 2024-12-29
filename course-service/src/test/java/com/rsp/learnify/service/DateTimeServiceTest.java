package com.rsp.learnify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateTimeServiceTest {

    private DateTimeService dateTimeService;

    @BeforeEach
    void setUp() {
        dateTimeService = new DateTimeService();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    void formatUpdatedDate_ShouldReturnFormattedDateTime() {
        Instant utcDateTime = Instant.parse("2023-10-15T14:30:00Z");

        String formattedDate = dateTimeService.formatUpdatedDate(utcDateTime);

        assertEquals("2023-10-15 14:30:00", formattedDate);
    }

    @Test
    void formatUpdatedDate_WithDifferentTimeZone_ShouldReturnFormattedDateTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        Instant utcDateTime = Instant.parse("2023-10-15T14:30:00Z");

        String formattedDate = dateTimeService.formatUpdatedDate(utcDateTime);

        assertEquals("2023-10-15 10:30:00", formattedDate);
    }
}