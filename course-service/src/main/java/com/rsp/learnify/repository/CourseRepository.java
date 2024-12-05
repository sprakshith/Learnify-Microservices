package com.rsp.learnify.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rsp.learnify.model.Course;

public interface CourseRepository extends MongoRepository<Course, String> {

    public List<Course> findByTeacherId(int teacherId);

}
