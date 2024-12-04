package com.rsp.learnify.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rsp.learnify.userservice.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final HttpServletRequest httpRequest;

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> isAdmin() {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(userService.isAdmin(token));
    }

    @GetMapping("/is-teacher")
    public ResponseEntity<Boolean> isTeacher() {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(userService.isTeacher(token));
    }

    @GetMapping("/is-student")
    public ResponseEntity<Boolean> isStudent() {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return ResponseEntity.ok(userService.isStudent(token));
    }

}
