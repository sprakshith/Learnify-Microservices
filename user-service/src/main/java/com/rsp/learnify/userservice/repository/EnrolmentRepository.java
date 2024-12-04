package com.rsp.learnify.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rsp.learnify.userservice.model.Enrolment;

public interface EnrolmentRepository extends JpaRepository<Enrolment, Integer> {

}
