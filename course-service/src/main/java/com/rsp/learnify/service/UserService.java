package com.rsp.learnify.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final WebClientService webClientService;

    public boolean isAdmin() throws Exception {
        return webClientService.userHasRole("admin");
    }

    public boolean isTeacher() throws Exception {
        return webClientService.userHasRole("teacher");
    }

    public boolean isStudent() throws Exception {
        return webClientService.userHasRole("student");
    }

    public Map<String, Object> getUserDetails() throws Exception {
        return webClientService.getUserDetails();
    }

    public List<String> getEnrolledCourses() throws Exception {
        return webClientService.getEnrolledCourses();
    }

}
