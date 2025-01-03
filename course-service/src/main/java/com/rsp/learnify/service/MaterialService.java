package com.rsp.learnify.service;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.rsp.learnify.dto.MaterialResponse;
import com.rsp.learnify.model.Course;
import com.rsp.learnify.model.Material;
import com.rsp.learnify.repository.CourseRepository;
import com.rsp.learnify.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

        private final GridFsTemplate gridFsTemplate;

        private final CourseRepository courseRepository;

        private final MaterialRepository materialRepository;

        private final UserService userService;

        private final NotificationService notificationService;

        private final DateTimeService dateTimeService;

        public void storeMaterial(String courseId, MultipartFile file) throws Exception {
                Map<String, Object> userDetails = userService.getUserDetails();

                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException(
                                                String.format("Course with id '%s' not found!", courseId)));

                if (!userService.isTeacher()) {
                        throw new Exception(
                                        "Unauthorized access! Only teachers are allowed to add materials to their courses.");
                } else if (!(course.getTeacherId() == Integer.parseInt(userDetails.get("id").toString()))) {
                        throw new Exception(
                                        "Unauthorized access! Only course owners are allowed to add materials to their course.");
                }

                try (InputStream inputStream = file.getInputStream()) {
                        GridFSUploadOptions options = new GridFSUploadOptions()
                                        .metadata(new org.bson.Document("type", file.getContentType()));

                        ObjectId fileId = gridFsTemplate.store(inputStream, file.getOriginalFilename(), options);

                        Material material = Material.builder()
                                        .order(1)
                                        .type(file.getContentType())
                                        .fileName(file.getOriginalFilename())
                                        .fileId(fileId.toString())
                                        .createdDate(Instant.now())
                                        .updatedDate(Instant.now())
                                        .build();

                        if (course.getMaterials() == null) {
                                course.setMaterials(new ArrayList<>());
                        }

                        course.getMaterials().add(material);

                        materialRepository.save(material);

                        courseRepository.save(course);

                        notificationService.courseUpdate(userDetails.get("id").toString(), courseId, course.getTitle());

                } catch (Exception e) {
                        throw new Exception(e.getMessage());
                }
        }

        public List<MaterialResponse> getMaterialsByCourseId(String courseId) throws Exception {
                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException("Course not found!"));

                List<Material> materials = course.getMaterials();

                if (materials == null || materials.isEmpty()) {
                        return List.of();
                }

                return materials.stream().map(this::mapToMaterialResponse).toList();
        }

        public ResponseEntity<InputStreamResource> downloadMaterial(@PathVariable String courseId,
                        @PathVariable String materialId) throws Exception {

                Map<String, Object> userDetails = userService.getUserDetails();

                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException("Course not found!"));

                if (userService.isStudent()) {
                        List<String> enrolledCourses = userService.getEnrolledCourses();

                        if (!enrolledCourses.contains(courseId)) {
                                throw new Exception(
                                                "Unauthorized access! You are not enrolled in this course. Please enroll to access materials.");
                        }

                } else if (userService.isTeacher()) {
                        if (course.getTeacherId() != Integer.parseInt(userDetails.get("id").toString())) {
                                throw new Exception(
                                                "Unauthorized access! Only course owners are allowed to access materials.");
                        }
                } else {
                        throw new Exception(
                                        "Unauthorized access! Only teachers and students are allowed to access materials.");
                }

                Material material = materialRepository.findById(materialId)
                                .orElseThrow(() -> new RuntimeException("Material not found!"));

                GridFSFile gridFSFile = gridFsTemplate
                                .findOne(new Query(Criteria.where("_id").is(material.getFileId())));

                GridFsResource resource = gridFsTemplate.getResource(gridFSFile);

                InputStream inputStream = resource.getInputStream();

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + material.getFileName() + "\"")
                                .contentType(MediaType.parseMediaType(material.getType()))
                                .body(new InputStreamResource(inputStream));
        }

        public void deleteMaterial(String courseId, String materialId) throws Exception {
                Map<String, Object> userDetails = userService.getUserDetails();

                if (!userService.isTeacher()) {
                        throw new Exception("Unauthorized access! Only teachers can delete materials.");
                }

                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException("Course not found!"));

                Material material = materialRepository.findById(materialId)
                                .orElseThrow(() -> new RuntimeException("Material not found!"));

                course.getMaterials().remove(material);

                courseRepository.save(course);

                materialRepository.delete(material);

                notificationService.courseUpdate(userDetails.get("id").toString(), courseId, course.getTitle());
        }

        public List<MaterialResponse> getAllCourseMaterials(String courseId) {
                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException("Course not found!"));

                List<Material> materials = course.getMaterials();

                if (materials == null || materials.isEmpty()) {
                        return List.of();
                }

                return materials.stream().map(this::mapToMaterialResponse).toList();
        }

        private MaterialResponse mapToMaterialResponse(Material material) {
                return MaterialResponse.builder()
                                .id(material.getId())
                                .order(material.getOrder())
                                .type(material.getType())
                                .fileName(material.getFileName())
                                .fileId(material.getFileId())
                                .updatedDate(dateTimeService.formatUpdatedDate(material.getUpdatedDate()))
                                .build();
        }

}
