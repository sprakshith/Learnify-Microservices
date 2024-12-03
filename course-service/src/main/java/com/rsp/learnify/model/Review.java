package com.rsp.learnify.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "reviews")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Review {

    @Id
    private String id;
    private String courseId;
    private Integer studentId;
    private Integer rating;
    private String comment;
    private Instant createdDate;
    private Instant updatedDate;

}
