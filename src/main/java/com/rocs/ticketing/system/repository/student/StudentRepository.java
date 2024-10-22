package com.rocs.ticketing.system.repository.student;

import com.rocs.ticketing.system.domain.student.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Students, Long> {

        boolean existsByStudentNumberAndEmail(String studentNumber, String email);

        Students findByStudentNumber(String studentNumber);

    @Query("SELECT s FROM Students s WHERE s.studentNumber = :userId")
    Optional<Students> findByUserId(@Param("userId") String userId);

    // Define the method to find by student number optionally

}
