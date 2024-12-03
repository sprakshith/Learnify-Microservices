package com.rsp.learnify.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Course {

    @Id
    private String id;
    private String title;
    private String description;
    private Integer teacherId;
    private Instant createdDate;
    private Instant updatedDate;

    @DBRef
    private List<Material> materials;

    @DBRef
    private List<Review> reviews;

}
