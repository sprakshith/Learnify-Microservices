package com.rsp.learnify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rsp.learnify.dto.CourseRequest;
import com.rsp.learnify.dto.CourseResponse;
import com.rsp.learnify.dto.FullCourseDetailsResponse;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ReviewService reviewService;
    @Mock
    private MaterialService materialService;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private DateTimeService dateTimeService;

    @InjectMocks
    private CourseService courseService;

    @Test
    void createCourse_WhenTeacher_ShouldCreateCourse() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .title("Java Course")
                .description("Learn Java")
                .build();

        Map<String, Object> userDetails = Map.of("id", "1");
        when(userService.isTeacher()).thenReturn(true);
        when(userService.getUserDetails()).thenReturn(userDetails);

        String result = courseService.createCourse(request);

        verify(courseRepository).save(any(Course.class));
        verify(notificationService).courseCreation(eq("1"), isNull(), eq("Java Course"));
        assertEquals("Course with title 'Java Course' successfully created!", result);
    }

    @Test
    void createCourse_WhenNotTeacher_ShouldThrowException() throws Exception {
        CourseRequest request = new CourseRequest();
        when(userService.isTeacher()).thenReturn(false);

        assertThrows(Exception.class, () -> courseService.createCourse(request));
    }

    @Test
    void getFullCourseDetailsById_WhenCourseExists_ShouldReturnDetails() {
        String courseId = "1";
        Course course = Course.builder()
                .id(courseId)
                .title("Java")
                .description("Learn Java")
                .teacherId(1)
                .updatedDate(Instant.now())
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(dateTimeService.formatUpdatedDate(any())).thenReturn("2023-10-15");
        when(materialService.getAllCourseMaterials(courseId)).thenReturn(List.of());

        FullCourseDetailsResponse response = courseService.getFullCourseDetailsById(courseId);

        assertNotNull(response);
        assertEquals(courseId, response.getId());
    }

    @Test
    void getAllCourses_ShouldReturnMappedCourses() {
        List<Course> courses = List.of(
                Course.builder().id("1").title("Java").build(),
                Course.builder().id("2").title("Python").build());
        when(courseRepository.findAll()).thenReturn(courses);
        when(dateTimeService.formatUpdatedDate(any())).thenReturn("2023-10-15");

        List<CourseResponse> result = courseService.getAllCourses();

        assertEquals(2, result.size());
    }

    @Test
    void getMyCourses_WhenTeacher_ShouldReturnTeacherCourses() throws Exception {
        Map<String, Object> userDetails = Map.of("id", "1");
        when(userService.isTeacher()).thenReturn(true);
        when(userService.getUserDetails()).thenReturn(userDetails);
        when(courseRepository.findByTeacherId(1)).thenReturn(List.of());

        List<CourseResponse> result = courseService.getMyCourses();

        assertNotNull(result);
        verify(courseRepository).findByTeacherId(1);
    }

    @Test
    void getTeacherIdByCourseId_WhenCourseExists_ShouldReturnTeacherId() {
        String courseId = "1";
        Course course = Course.builder().teacherId(1).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Integer teacherId = courseService.getTeacherIdByCourseId(courseId);

        assertEquals(1, teacherId);
    }

    @Test
    void getTeacherIdByCourseId_WhenCourseNotFound_ShouldThrowException() {
        String courseId = "1";
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.getTeacherIdByCourseId(courseId));
    }
}