package com.rsp.learnify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private WebClientService webClientService;

    @InjectMocks
    private UserService userService;

    @Test
    void isAdmin_ShouldReturnTrue_WhenUserHasAdminRole() throws Exception {
        when(webClientService.userHasRole("admin")).thenReturn(true);
        assertTrue(userService.isAdmin());
    }

    @Test
    void isTeacher_ShouldReturnTrue_WhenUserHasTeacherRole() throws Exception {
        when(webClientService.userHasRole("teacher")).thenReturn(true);
        assertTrue(userService.isTeacher());
    }

    @Test
    void isStudent_ShouldReturnTrue_WhenUserHasStudentRole() throws Exception {
        when(webClientService.userHasRole("student")).thenReturn(true);
        assertTrue(userService.isStudent());
    }

    @Test
    void getUserDetails_ShouldReturnUserDetails() throws Exception {
        Map<String, Object> expectedDetails = Map.of("id", "1", "name", "John");
        when(webClientService.getUserDetails()).thenReturn(expectedDetails);

        assertEquals(expectedDetails, userService.getUserDetails());
    }

    @Test
    void getEnrolledCourses_ShouldReturnCoursesList() throws Exception {
        List<String> expectedCourses = List.of("course1", "course2");
        when(webClientService.getEnrolledCourses()).thenReturn(expectedCourses);

        assertEquals(expectedCourses, userService.getEnrolledCourses());
    }

    @Test
    void isAdmin_ShouldThrowException_WhenWebClientFails() throws Exception {
        when(webClientService.userHasRole("admin")).thenThrow(new Exception("Network error"));
        assertThrows(Exception.class, () -> userService.isAdmin());
    }

    @Test
    void getUserDetails_ShouldThrowException_WhenWebClientFails() throws Exception {
        when(webClientService.getUserDetails()).thenThrow(new Exception("Network error"));
        assertThrows(Exception.class, () -> userService.getUserDetails());
    }

    @Test
    void getEnrolledCourses_ShouldThrowException_WhenWebClientFails() throws Exception {
        when(webClientService.getEnrolledCourses()).thenThrow(new Exception("Network error"));
        assertThrows(Exception.class, () -> userService.getEnrolledCourses());
    }
}