package com.example.notes.resource;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.ZonedDateTime;

@Data
public abstract class BaseResource {

    @Id
    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
}
