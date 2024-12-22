package com.example.notes.repository;

import com.example.notes.entity.User;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "notes", path = "api/notes")
public class NoteRepository extends SimpleMongoRepository<User, String> {
    public NoteRepository(MongoEntityInformation<User, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }
}
