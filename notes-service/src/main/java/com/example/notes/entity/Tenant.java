package com.example.notes.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "tenants")
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {

    @NotNull
    private String name;
    @NotNull
    @Indexed(unique = true)
    private String tenantId;
}

