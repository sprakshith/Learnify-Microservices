package com.rsp.learnify.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullCourseDetailsResponse {
    private String id;
    private String title;
    private String description;
    private Integer teacherId;
    private String updatedOn;
    private List<MaterialResponse> materials;
    private List<ReviewResponse> reviews;
}
