package com.rsp.learnify.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rsp.learnify.model.Material;

public interface MaterialRepository extends MongoRepository<Material, String> {

}
