package com.rsp.learnify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private String id;
    private Integer studentId;
    private Integer rating;
    private String comment;
    private String updatedOn;

}
