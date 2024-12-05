package com.rsp.learnify.userservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rsp.learnify.userservice.dto.UserResponse;
import com.rsp.learnify.userservice.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin() {
        try {
            return ResponseEntity.ok(userService.isAdmin());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/is-teacher")
    public ResponseEntity<Boolean> isTeacher() {
        try {
            return ResponseEntity.ok(userService.isTeacher());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/is-student")
    public ResponseEntity<Boolean> isStudent() {
        try {
            return ResponseEntity.ok(userService.isStudent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/get-user-details")
    public ResponseEntity<UserResponse> getUserDetails() {
        try {
            return ResponseEntity.ok(userService.getUserDetails());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/enrol/{courseId}")
    public ResponseEntity<String> enrol(@PathVariable String courseId) {
        try {
            userService.enrol(courseId);
            return ResponseEntity.ok("Enrolled successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-enrolled-courses")
    public ResponseEntity<List<String>> getEnrolledCourses() {
        try {
            return ResponseEntity.ok(userService.getEnrolledCourses());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
