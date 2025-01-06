package com.example.notes.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Represents an audit record for a resource.
 */
@Data
@Document(collection = "resource_audits")
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAudit implements Serializable {

    @Id
    private String id;
    private String entityName;
    private ResourceAction action;
    private BaseResource oldState;
    private BaseResource newState;
    private String actionedBy;
    private ZonedDateTime actionedOn;

}
