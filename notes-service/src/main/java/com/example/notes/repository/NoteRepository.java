package com.example.notes.repository;

import com.example.notes.resource.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "notes", path = "notes")
public interface NoteRepository extends MongoRepository<Note, String> {
}
