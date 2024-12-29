package com.rsp.learnify.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rsp.learnify.userservice.dto.UserResponse;
import com.rsp.learnify.userservice.model.Role;
import com.rsp.learnify.userservice.model.User;
import com.rsp.learnify.userservice.repository.EnrolmentRepository;
import com.rsp.learnify.userservice.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private WebClientService webClientService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EnrolmentRepository enrolmentRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsAdmin() throws Exception {
        User user = new User();
        user.setRole(Role.ADMIN);

        when(webClientService.getJWT()).thenReturn("test-token");
        when(jwtService.extractUsername("test-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean isAdmin = userService.isAdmin();

        assertEquals(true, isAdmin);
    }

    @Test
    public void testGetUserDetails() throws Exception {
        User user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");

        when(webClientService.getJWT()).thenReturn("test-token");
        when(jwtService.extractUsername("test-token")).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        UserResponse userResponse = userService.getUserDetails();

        assertEquals(1, userResponse.getId());
        assertEquals("John", userResponse.getFirstname());
        assertEquals("Doe", userResponse.getLastname());
        assertEquals("john.doe@example.com", userResponse.getEmail());
    }

    @Test
    public void testEnrol() throws Exception {
        User user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");

        when(webClientService.getJWT()).thenReturn("test-token");
        when(jwtService.extractUsername("test-token")).thenReturn("john.doe@example.com");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        userService.enrol("course-1");
    }
}
