package com.rsp.learnify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;

import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.repository.CourseRepository;
import com.rsp.learnify.repository.MaterialRepository;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private GridFsTemplate gridFsTemplate;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private DateTimeService dateTimeService;

    @InjectMocks
    private MaterialService materialService;

    @Test
    void storeMaterial_WhenTeacherAndOwner_ShouldStoreSuccessfully() throws Exception {
        String courseId = "1";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes());

        Course course = Course.builder()
                .id(courseId)
                .teacherId(1)
                .build();

        when(userService.isTeacher()).thenReturn(true);
        when(userService.getUserDetails()).thenReturn(Map.of("id", "1"));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(gridFsTemplate.store(
                any(InputStream.class),
                anyString(),
                any(GridFSUploadOptions.class)))
                .thenReturn(new ObjectId());

        materialService.storeMaterial(courseId, file);

        verify(gridFsTemplate).store(
                any(InputStream.class),
                eq(file.getOriginalFilename()),
                any(GridFSUploadOptions.class));
    }

    @Test
    void storeMaterial_WhenNotTeacher_ShouldThrowException() throws Exception {
        String courseId = "1";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test".getBytes());

        Course course = Course.builder()
                .id(courseId)
                .teacherId(1)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(userService.isTeacher()).thenReturn(false);
        Exception exception = assertThrows(Exception.class,
                () -> materialService.storeMaterial(courseId, file));

        assertEquals("Unauthorized access! Only teachers are allowed to add materials to their courses.",
                exception.getMessage());
    }

    @Test
    void storeMaterial_WhenDifferentTeacher_ShouldThrowException() throws Exception {
        String courseId = "1";
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test".getBytes());
        Course course = Course.builder()
                .id(courseId)
                .teacherId(2)
                .build();

        when(userService.isTeacher()).thenReturn(true);
        when(userService.getUserDetails()).thenReturn(Map.of("id", "1"));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        assertThrows(Exception.class, () -> materialService.storeMaterial(courseId, file));
    }

    @Test
    void storeMaterial_WhenCourseNotFound_ShouldThrowException() {
        String courseId = "1";
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test".getBytes());
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> materialService.storeMaterial(courseId, file));
    }
}