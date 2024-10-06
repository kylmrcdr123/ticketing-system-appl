package com.rocs.ticketing.system.repository.student;

import com.rocs.ticketing.system.domain.student.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Students, Long> {

        boolean existsByStudentNumberAndEmail(String studentNumber, String email);

        Students findByStudentNumber(String studentNumber);

    }
