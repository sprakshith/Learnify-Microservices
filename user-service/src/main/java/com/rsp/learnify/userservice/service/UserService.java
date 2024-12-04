package com.rsp.learnify.userservice.service;

import org.springframework.stereotype.Service;

import com.rsp.learnify.userservice.model.Role;
import com.rsp.learnify.userservice.model.User;
import com.rsp.learnify.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private boolean hasRole(String jwt, Role role) {
        String email = jwtService.extractUsername(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return user.getRole().equals(role);
    }

    public boolean isAdmin(String jwt) {
        return hasRole(jwt, Role.ADMIN);
    }

    public boolean isTeacher(String jwt) {
        return hasRole(jwt, Role.TEACHER);
    }

    public boolean isStudent(String jwt) {
        return hasRole(jwt, Role.STUDENT);
    }

}
