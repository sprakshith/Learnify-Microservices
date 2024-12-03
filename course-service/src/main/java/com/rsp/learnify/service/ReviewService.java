package com.rsp.learnify.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rsp.learnify.dto.ReviewRequest;
import com.rsp.learnify.dto.ReviewResponse;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.model.Review;
import com.rsp.learnify.repository.CourseRepository;
import com.rsp.learnify.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final CourseRepository courseRepository;

    private final ReviewRepository reviewRepository;

    public void createReview(ReviewRequest reviewRequest) {
        Course course = courseRepository.findById(reviewRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Review review = Review.builder()
                .courseId(reviewRequest.getCourseId())
                .studentId(1) // TODO: Hardcoded for now
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();

        if (course.getReviews() == null) {
            course.setReviews(new ArrayList<>());
        }

        course.getReviews().add(review);

        reviewRepository.save(review);

        courseRepository.save(course);
    }

    public List<ReviewResponse> getReviewsByCourseId(String courseId) {
        List<Review> reviews = reviewRepository.findByCourseId(courseId);

        return reviews.stream().map(this::mapToReviewResponse).toList();
    }

    public void updateReview(String id, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(id).orElseThrow();

        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setUpdatedDate(Instant.now());

        reviewRepository.save(review);
    }

    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Course course = courseRepository.findById(review.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.getReviews().remove(review);

        courseRepository.save(course);

        reviewRepository.deleteById(reviewId);
    }

    public ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .studentId(review.getStudentId())
                .rating(review.getRating())
                .comment(review.getComment())
                .updatedOn(formatUpdatedDate(review.getUpdatedDate()))
                .build();
    }

    private String formatUpdatedDate(Instant utcDateTime) {
        ZoneId userZoneId = ZoneId.systemDefault();
        ZonedDateTime userLocalTime = utcDateTime.atZone(userZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return userLocalTime.format(formatter);
    }

}
