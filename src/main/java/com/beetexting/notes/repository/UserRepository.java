package com.beetexting.notes.repository;

import com.beetexting.notes.entity.User;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "users", path = "api/users")
public class UserRepository extends SimpleMongoRepository<User, String> {
    public UserRepository(MongoEntityInformation<User, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }
}
