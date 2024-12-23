package com.example.notes.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;

@Data
public class BaseEntity {

    @Id
    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
}
