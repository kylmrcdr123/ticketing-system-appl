package com.rocs.ticketing.system.service.student;

import com.rocs.ticketing.system.domain.student.Students;

import java.util.List;
public interface StudentService { List<Students> getAllStudent();
    Students addStudent(Students student);
    Students getStudentByNumber(String studentNumber);
    Students updateStudent(Long id, Students student);

}
