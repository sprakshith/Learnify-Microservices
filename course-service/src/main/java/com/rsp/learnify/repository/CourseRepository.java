package com.rsp.learnify.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rsp.learnify.model.Course;

public interface CourseRepository extends MongoRepository<Course, String> {

}
