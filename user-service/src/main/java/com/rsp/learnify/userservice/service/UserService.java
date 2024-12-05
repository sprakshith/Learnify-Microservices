package com.rsp.learnify.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rsp.learnify.userservice.dto.UserResponse;
import com.rsp.learnify.userservice.model.Enrolment;
import com.rsp.learnify.userservice.model.Role;
import com.rsp.learnify.userservice.model.User;
import com.rsp.learnify.userservice.repository.EnrolmentRepository;
import com.rsp.learnify.userservice.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final EnrolmentRepository enrolmentRepository;

    private final JwtService jwtService;

    private final HttpServletRequest httpRequest;

    private boolean hasRole(Role role) throws Exception {
        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return user.getRole().equals(role);
    }

    public boolean isAdmin() throws Exception {
        return hasRole(Role.ADMIN);
    }

    public boolean isTeacher() throws Exception {
        return hasRole(Role.TEACHER);
    }

    public boolean isStudent() throws Exception {
        return hasRole(Role.STUDENT);
    }

    public void enrol(String courseId) throws Exception {
        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        try {
            Enrolment enrolment = Enrolment.builder()
                    .courseId(courseId)
                    .studentId(user.getId())
                    .build();

            enrolmentRepository.save(enrolment);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public UserResponse getUserDetails() throws Exception {
        String token;

        try {
            token = httpRequest.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            throw new Exception("Authentication token not found!");
        }

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }

    public List<String> getEnrolledCourses() {
        String token = httpRequest.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        List<Enrolment> enrolments = enrolmentRepository.findByStudentId(user.getId());

        return enrolments.stream().map(Enrolment::getCourseId).toList();
    }

}
