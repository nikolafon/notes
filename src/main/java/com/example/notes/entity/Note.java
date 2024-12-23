package com.example.notes.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "#{@tenantCollectionNameResolver.getTenantCollectionName('notes')}")
public class Note extends BaseEntity {
    @NotEmpty
    @Size(max = 100)
    private String title;
    @NotEmpty
    @Size(max = 4000)
    private String content;
}
