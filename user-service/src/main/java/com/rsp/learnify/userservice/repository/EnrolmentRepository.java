package com.rsp.learnify.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rsp.learnify.userservice.model.Enrolment;

public interface EnrolmentRepository extends JpaRepository<Enrolment, Integer> {

    public List<Enrolment> findByStudentId(Integer studentId);

}
