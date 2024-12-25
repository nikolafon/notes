package com.example.notes.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Data
@Document(collection = "resource_audits")
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAudit {

    @Id
    private String id;
    private String entityName;
    private ResourceAction action;
    private BaseResource oldState;
    private BaseResource newState;
    private String actionedBy;
    private ZonedDateTime actionedOn;

}
