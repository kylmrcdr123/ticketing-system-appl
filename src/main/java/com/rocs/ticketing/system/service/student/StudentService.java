package com.rocs.ticketing.system.service.student;

import com.rocs.ticketing.system.domain.student.Students;

import java.util.List;
import java.util.Optional;

public interface StudentService { List<Students> getAllStudent();
    Students addStudent(Students student);
    Students getStudentByNumber(String studentNumber);
    Students updateStudents(String userId, Students student);

    Students getStudentByUserId(String userId);
    Optional<Students> findById(Long id);


}
