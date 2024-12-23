package com.example.notes.entity;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "notes")
public class Note extends BaseEntity {
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
