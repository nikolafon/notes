package com.example.notes.resource;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Data
@Document(collection = "tenants")
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseResource {

    @NotNull
    private String name;
    @NotEmpty
    @Indexed(unique = true)
    private String tenantId;
}

