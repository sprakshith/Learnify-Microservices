package com.rsp.learnify.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private final UserService userService;

    public String createCourse(CourseRequest courseRequest) throws Exception {
        if (!userService.isTeacher()) {
            throw new Exception("Unauthorized access! Only teachers can create courses.");
        }

        Map<String, Object> userDetails = userService.getUserDetails();

        try {
            Course course = Course.builder()
                    .title(courseRequest.getTitle())
                    .description(courseRequest.getDescription())
                    .teacherId(Integer.parseInt(userDetails.get("id").toString()))
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

    public List<CourseResponse> getMyCourses() throws Exception {
        Map<String, Object> userDetails = userService.getUserDetails();

        List<Course> courses = courseRepository.findByTeacherId(Integer.parseInt(userDetails.get("id").toString()));

        return courses.stream().map(this::mapToCourseResponse).toList();
    }

    public List<CourseResponse> getEnrolledCourses() throws Exception {
        List<String> courseIds = userService.getEnrolledCourses();

        List<Optional<Course>> courses = courseIds.stream().map(courseRepository::findById).toList();

        return courses.stream().filter(Optional::isPresent).map(course -> mapToCourseResponse(course.get())).toList();
    }

}
