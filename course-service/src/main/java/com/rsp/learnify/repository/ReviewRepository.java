package com.rsp.learnify.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.rsp.learnify.model.Review;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByCourseId(String courseId);

}
