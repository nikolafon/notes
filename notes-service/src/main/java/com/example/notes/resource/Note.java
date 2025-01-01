package com.example.notes.resource;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Data
@Document(collection = "notes")
public class Note extends BaseResource {
    @Indexed(unique = true)
    private String tenantId;
    @NotEmpty
    @Size(max = 100)
    private String title;
    @NotEmpty
    @Size(max = 4000)
    private String content;
    @NotEmpty
    @Size(max = 100)
    private String owner;
    private Set<String> collaborators = new HashSet<>();
}
