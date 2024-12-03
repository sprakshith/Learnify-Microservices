package com.rsp.learnify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rsp.learnify.model.Review;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByCourseId(String courseId);

    Optional<Review> findByCourseIdAndStudentId(String courseId, Integer studentId);

}
