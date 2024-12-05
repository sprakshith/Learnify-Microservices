package com.rsp.learnify.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rsp.learnify.dto.CourseRequest;
import com.rsp.learnify.dto.CourseResponse;
import com.rsp.learnify.dto.MaterialResponse;
import com.rsp.learnify.dto.ReviewRequest;
import com.rsp.learnify.dto.ReviewResponse;
import com.rsp.learnify.service.CourseService;
import com.rsp.learnify.service.MaterialService;
import com.rsp.learnify.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    private final ReviewService reviewService;

    private final MaterialService materialService;

    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody CourseRequest courseRequest) {
        try {
            return new ResponseEntity<>(courseService.createCourse(courseRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/my-courses")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CourseResponse>> getMyCourses() {
        try {
            return new ResponseEntity<>(courseService.getMyCourses(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/enrolled-courses")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CourseResponse>> getEnrolledCourses() {
        try {
            return new ResponseEntity<>(courseService.getEnrolledCourses(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{courseId}/materials/upload")
    public ResponseEntity<String> uploadFile(@PathVariable String courseId, @RequestParam("file") MultipartFile file) {
        try {
            materialService.storeMaterial(courseId, file);
            return new ResponseEntity<>("Successfully added material!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{courseId}/materials")
    public List<MaterialResponse> getMaterialsByCourseId(@PathVariable String courseId) {
        try {
            return materialService.getMaterialsByCourseId(courseId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/{courseId}/materials/download/{materialId}")
    public ResponseEntity<InputStreamResource> downloadMaterial(@PathVariable String courseId,
            @PathVariable String materialId) {
        try {
            return materialService.downloadMaterial(courseId, materialId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{courseId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createReview(@PathVariable String courseId,
            @RequestBody ReviewRequest reviewRequest) {
        try {
            reviewService.createReview(courseId, reviewRequest);
            return new ResponseEntity<>("Review added successfully!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{courseId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByCourseId(@PathVariable String courseId) {
        return courseService.getAllReviews(courseId);
    }

    @PutMapping("/{courseId}/reviews/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable String courseId, @PathVariable String reviewId,
            @RequestBody ReviewRequest reviewRequest) {
        try {
            reviewService.updateReview(courseId, reviewId, reviewRequest);
            return new ResponseEntity<>("Successfully updated your review!", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{courseId}/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable String courseId, @PathVariable String reviewId) {
        try {
            reviewService.deleteReview(courseId, reviewId);
            return new ResponseEntity<>("Successfully deleted your review!", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
