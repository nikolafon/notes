package com.example.notes.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResourceRepository {

    public static final String RESOURCE_FIND_CACHE = "resourceFindCache";
    public static final String RESOURCE_CRUD_CACHE = "resourceCrudCache";
    public static final String RESOURCE_CRUD_CACHE_KEY = "#resource.getId() + '-' + #resource.getClass().getSimpleName()";
    public static final String RESOURCE_ID_CRUD_CACHE_KEY = "#id + '-' + #resourceClass.getSimpleName()";
    @Autowired
    private MongoTemplate mongoTemplate;

    @Cacheable(cacheNames = RESOURCE_FIND_CACHE)
    public <R> Page<R> find(Query query, Pageable pageable, Class<R> resourceClass) {
        query.with(pageable);
        long total = mongoTemplate.count(query, resourceClass);
        List<R> products = mongoTemplate.find(query, resourceClass);
        return new PageImpl<>(products, pageable, total);
    }

    @Cacheable(cacheNames = RESOURCE_FIND_CACHE)
    public <R> List<R> find(Query query, Class<R> resourceClass) {
        return mongoTemplate.find(query, resourceClass);
    }

    @CachePut(cacheNames = RESOURCE_CRUD_CACHE, key = RESOURCE_CRUD_CACHE_KEY)
    @CacheEvict(cacheNames = RESOURCE_FIND_CACHE, allEntries = true)
    public <R> R create(R resource) {
        return mongoTemplate.insert(resource);
    }

    @CachePut(cacheNames = RESOURCE_CRUD_CACHE, key = RESOURCE_CRUD_CACHE_KEY)
    @CacheEvict(cacheNames = RESOURCE_FIND_CACHE, allEntries = true)
    public <R> R update(R resource) {
        return mongoTemplate.save(resource);
    }

    @Caching(evict =
            {@CacheEvict(value = RESOURCE_FIND_CACHE, key = RESOURCE_ID_CRUD_CACHE_KEY),
                    @CacheEvict(value = RESOURCE_FIND_CACHE, allEntries = true)})
    public <R> void delete(String id, Class<R> resourceClass) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), resourceClass);
    }

}
