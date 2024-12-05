package com.rsp.learnify.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private final UserService userService;

    public void createReview(String courseId, ReviewRequest reviewRequest) throws Exception {

        if (userService.isStudent()) {
            List<String> enrolledCourses = userService.getEnrolledCourses();

            if (!enrolledCourses.contains(courseId)) {
                throw new Exception(
                        "Unauthorized access! You are not enrolled in this course. Please enroll and go through the course before adding a review.");
            }
        } else {
            throw new Exception("Unauthorized access! Only students can add a review.");
        }

        Map<String, Object> userDetails = userService.getUserDetails();
        Integer userId = Integer.parseInt(userDetails.get("id").toString());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        Review existingReview = reviewRepository.findByCourseIdAndStudentId(courseId, userId)
                .orElse(null);

        if (existingReview != null) {
            throw new RuntimeException("Review already exists for this course!");
        }

        Review review = Review.builder()
                .courseId(courseId)
                .studentId(userId)
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

    public void updateReview(String courseId, String reviewId, ReviewRequest reviewRequest) throws Exception {
        Review review = null;

        Map<String, Object> userDetails = userService.getUserDetails();

        if (userService.isStudent()) {
            review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found!"));

            if (review.getStudentId() != Integer.parseInt(userDetails.get("id").toString())) {
                throw new Exception("Unauthorized access! You can only edit your review.");
            }

        } else {
            throw new Exception("Unauthorized access! Only students can edit their review.");
        }

        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setUpdatedDate(Instant.now());

        reviewRepository.save(review);
    }

    public void deleteReview(String courseId, String reviewId) throws Exception {

        Review review = null;

        Map<String, Object> userDetails = userService.getUserDetails();

        if (userService.isStudent()) {
            review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found!"));

            if (review.getStudentId() != Integer.parseInt(userDetails.get("id").toString())) {
                throw new Exception("Unauthorized access! You can only delete your review.");
            }

        } else {
            throw new Exception("Unauthorized access! Only students can delete their review.");
        }

        Course course = courseRepository.findById(courseId)
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
