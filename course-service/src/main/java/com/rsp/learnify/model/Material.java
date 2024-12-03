package com.rsp.learnify.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "materials")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Material {

    @Id
    private String id;
    private Integer order;
    private String type;
    private String fileName;
    private String fileId;
    private Instant createdDate;
    private Instant updatedDate;

}
