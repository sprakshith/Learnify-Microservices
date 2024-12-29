package com.rsp.learnify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rsp.learnify.dto.ReviewRequest;
import com.rsp.learnify.dto.ReviewResponse;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.model.Review;
import com.rsp.learnify.repository.CourseRepository;
import com.rsp.learnify.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserService userService;
    @Mock
    private DateTimeService dateTimeService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReview_WhenStudentEnrolled_ShouldCreateReview() throws Exception {
        String courseId = "course1";
        ReviewRequest request = ReviewRequest.builder()
                .rating(5)
                .comment("Great course!")
                .build();

        when(userService.isStudent()).thenReturn(true);
        when(userService.getEnrolledCourses()).thenReturn(List.of(courseId));
        when(userService.getUserDetails()).thenReturn(Map.of("id", "1"));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(Course.builder()
                .id(courseId)
                .reviews(new ArrayList<>())
                .build()));
        when(reviewRepository.findByCourseIdAndStudentId(courseId, 1)).thenReturn(Optional.empty());

        reviewService.createReview(courseId, request);

        verify(reviewRepository).save(any(Review.class));
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createReview_WhenNotStudent_ShouldThrowException() throws Exception {
        when(userService.isStudent()).thenReturn(false);

        Exception exception = assertThrows(Exception.class,
                () -> reviewService.createReview("course1", new ReviewRequest()));
        assertEquals("Unauthorized access! Only students can add a review.", exception.getMessage());
    }

    @Test
    void getReviewsByCourseId_ShouldReturnMappedReviews() {
        String courseId = "course1";
        Review review = Review.builder()
                .id("review1")
                .studentId(1)
                .rating(5)
                .comment("Great!")
                .updatedDate(Instant.now())
                .build();

        when(reviewRepository.findByCourseId(courseId)).thenReturn(List.of(review));
        when(dateTimeService.formatUpdatedDate(any())).thenReturn("2024-03-20 10:00:00");

        List<ReviewResponse> result = reviewService.getReviewsByCourseId(courseId);

        assertEquals(1, result.size());
        assertEquals("review1", result.get(0).getId());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void updateReview_WhenOwnReview_ShouldUpdate() throws Exception {
        String reviewId = "review1";
        ReviewRequest request = ReviewRequest.builder()
                .rating(4)
                .comment("Updated comment")
                .build();

        Review existingReview = Review.builder()
                .id(reviewId)
                .studentId(1)
                .build();

        when(userService.isStudent()).thenReturn(true);
        when(userService.getUserDetails()).thenReturn(Map.of("id", "1"));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));

        reviewService.updateReview("course1", reviewId, request);

        verify(reviewRepository).save(existingReview);
        assertEquals(4, existingReview.getRating());
        assertEquals("Updated comment", existingReview.getComment());
    }

    @Test
    void deleteReview_WhenOwnReview_ShouldDelete() throws Exception {
        String courseId = "course1";
        String reviewId = "review1";

        Review review = Review.builder()
                .id(reviewId)
                .studentId(1)
                .build();

        Course course = Course.builder()
                .id(courseId)
                .reviews(new ArrayList<>(List.of(review)))
                .build();

        when(userService.isStudent()).thenReturn(true);
        when(userService.getUserDetails()).thenReturn(Map.of("id", "1"));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        reviewService.deleteReview(courseId, reviewId);

        verify(courseRepository).save(course);
        verify(reviewRepository).deleteById(reviewId);
        assertTrue(course.getReviews().isEmpty());
    }
}