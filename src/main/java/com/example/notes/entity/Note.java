package com.example.notes.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

@EqualsAndHashCode(callSuper = true)
@Data
public class Note extends BaseEntity {
    @Id
    private String id;
    private String title;
    private String content;
}
