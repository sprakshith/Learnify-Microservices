package com.rsp.learnify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialResponse {
    private String id;
    private Integer order;
    private String type;
    private String fileName;
    private String fileId;
    private String updatedDate;
}
