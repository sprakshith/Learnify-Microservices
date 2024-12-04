package com.rsp.learnify.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rsp.learnify.dto.CourseRequest;
import com.rsp.learnify.dto.CourseResponse;
import com.rsp.learnify.dto.ReviewResponse;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.model.Review;
import com.rsp.learnify.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final ReviewService reviewService;

    private final RoleService roleService;

    public String createCourse(CourseRequest courseRequest) throws Exception {
        if (!roleService.isTeacher()) {
            throw new Exception("Unauthorized access! Only teachers can create courses.");
        }

        try {
            Course course = Course.builder()
                    .title(courseRequest.getTitle())
                    .description(courseRequest.getDescription())
                    .teacherId(courseRequest.getTeacherId()) // TODO: Get teacher ID from token
                    .createdDate(Instant.now())
                    .updatedDate(Instant.now())
                    .build();

            courseRepository.save(course);

            return String.format("Course with title '%s' successfully created!", courseRequest.getTitle());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(this::mapToCourseResponse).toList();
    }

    public List<ReviewResponse> getAllReviews(String id) {
        Course course = courseRepository.findById(id).orElseThrow();

        List<Review> reviews = course.getReviews();

        if (reviews == null || reviews.isEmpty()) {
            return List.of();
        }

        return reviews.stream().map(review -> reviewService.mapToReviewResponse(review)).toList();
    }

    private CourseResponse mapToCourseResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacherId())
                .updatedOn(formatUpdatedDate(course.getUpdatedDate()))
                .build();
    }

    private String formatUpdatedDate(Instant utcDateTime) {
        ZoneId userZoneId = ZoneId.systemDefault();
        ZonedDateTime userLocalTime = utcDateTime.atZone(userZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return userLocalTime.format(formatter);
    }

}
