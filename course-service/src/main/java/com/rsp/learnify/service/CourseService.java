package com.rsp.learnify.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.rsp.learnify.dto.CourseRequest;
import com.rsp.learnify.dto.CourseResponse;
import com.rsp.learnify.dto.FullCourseDetailsResponse;
import com.rsp.learnify.dto.MaterialResponse;
import com.rsp.learnify.dto.ReviewResponse;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.model.Review;
import com.rsp.learnify.repository.CourseRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final ReviewService reviewService;

    private final MaterialService materialService;

    private final UserService userService;

    private final WebClient.Builder webClientBuilder;

    private final HttpServletRequest httpRequest;

    public String createCourse(CourseRequest courseRequest) throws Exception {
        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

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

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("from", userDetails.get("id").toString());
            requestBody.put("to", "My Students");
            requestBody.put("type", "NEW COURSE");
            requestBody.put("courseId", course.getId());
            requestBody.put("message",
                    String.format(
                            "I am happy to announce that I have launched a new course with the title '%s'. Check out soon!",
                            courseRequest.getTitle()));

            webClientBuilder.build()
                    .post()
                    .uri("http://notification-service/api/v1/notifications/courses/created")
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(requestBody)
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe();

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
                .updatedOn(formatUpdatedDate(course.getUpdatedDate()))
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

    public Integer getTeacherIdByCourseId(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        return course.getTeacherId();
    }

}
