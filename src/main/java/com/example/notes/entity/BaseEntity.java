package com.example.notes.entity;

import java.time.ZonedDateTime;

public class BaseEntity {

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
