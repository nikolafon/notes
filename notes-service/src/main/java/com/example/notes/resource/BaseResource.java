package com.example.notes.resource;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Base class for all resources.
 */
@Data
@FieldNameConstants
public abstract class BaseResource implements Serializable {

    @Id
    @NotEmpty
    private String id;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
    private String createdBy;
    private String updatedBy;
}
