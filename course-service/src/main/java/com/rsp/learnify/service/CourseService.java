package com.rsp.learnify.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rsp.learnify.dto.CourseRequest;
import com.rsp.learnify.dto.CourseResponse;
import com.rsp.learnify.dto.FullCourseDetailsResponse;
import com.rsp.learnify.dto.MaterialResponse;
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

    private final MaterialService materialService;

    private final UserService userService;

    private final NotificationService notificationService;

    private final DateTimeService dateTimeService;

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

            notificationService.courseCreation(userDetails.get("id").toString(), course.getId(), course.getTitle());

            return String.format("Course with title '%s' successfully created!", courseRequest.getTitle());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public FullCourseDetailsResponse getFullCourseDetailsById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        List<ReviewResponse> reviews = getAllReviews(courseId);

        List<MaterialResponse> materials = materialService.getAllCourseMaterials(courseId);

        return FullCourseDetailsResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacherId())
                .updatedOn(dateTimeService.formatUpdatedDate(course.getUpdatedDate()))
                .materials(materials)
                .reviews(reviews)
                .build();
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
                .updatedOn(dateTimeService.formatUpdatedDate(course.getUpdatedDate()))
                .build();
    }

    public List<CourseResponse> getMyCourses() throws Exception {
        if (userService.isTeacher()) {
            Map<String, Object> userDetails = userService.getUserDetails();

            List<Course> courses = courseRepository.findByTeacherId(Integer.parseInt(userDetails.get("id").toString()));

            return courses.stream().map(this::mapToCourseResponse).toList();
        } else if (userService.isStudent()) {
            List<String> courseIds = userService.getEnrolledCourses();

            List<Optional<Course>> courses = courseIds.stream().map(courseRepository::findById).toList();

            return courses.stream().filter(Optional::isPresent).map(course -> mapToCourseResponse(course.get()))
                    .toList();
        } else {
            throw new Exception("Unauthorized access! Only teachers and students can access this resource.");
        }
    }

    public Integer getTeacherIdByCourseId(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        return course.getTeacherId();
    }

}
