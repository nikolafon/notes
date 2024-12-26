package com.example.notes.resource;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "#{@tenantCollectionNameResolver.getTenantCollectionName('notes')}")
public class Note extends BaseResource {
    @NotEmpty
    @Size(max = 100)
    private String title;
    @NotEmpty
    @Size(max = 4000)
    private String content;
    @NotEmpty
    @Size(max = 100)
    private String owner;
    @NotNull
    private Set<String> collaborators = new HashSet<>();
}
