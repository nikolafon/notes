package com.example.notes.repository;

import com.example.notes.resource.ResourceAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "resourceaudits", path = "resourceaudits")
public interface ResourceAuditRepository extends MongoRepository<ResourceAudit, String> {
}
