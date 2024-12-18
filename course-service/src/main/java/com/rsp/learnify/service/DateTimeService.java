package com.rsp.learnify.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

@Service
public class DateTimeService {

    public String formatUpdatedDate(Instant utcDateTime) {
        ZoneId userZoneId = ZoneId.systemDefault();
        ZonedDateTime userLocalTime = utcDateTime.atZone(userZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return userLocalTime.format(formatter);
    }

}
