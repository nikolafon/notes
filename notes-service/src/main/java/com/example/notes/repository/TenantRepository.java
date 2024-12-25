package com.example.notes.repository;

import com.example.notes.resource.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "tenants", path = "tenants")
public interface TenantRepository extends MongoRepository<Tenant, String> {
}
