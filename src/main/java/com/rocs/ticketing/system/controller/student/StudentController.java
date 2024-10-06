package com.rocs.ticketing.system.controller.student;

import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/StudentService")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public ResponseEntity<List<Students>> getAllStudent() {
        return new ResponseEntity<>(studentService.getAllStudent(), HttpStatus.OK);
    }

    @PostMapping("/student/add")
    public ResponseEntity<String> addStudent(@RequestBody Students student) {
        try {
            Students newStudent= studentService.addStudent(student);
            return new ResponseEntity<>("Student successfully added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Student cannot be added", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/student/update/{studentId}")
    public ResponseEntity<String> updateStudent(@PathVariable("studentId") Long id, @RequestBody Students student) {
        try {
            Students updatedStudent = studentService.updateStudent(id, student);
            if (updatedStudent != null) {
                return new ResponseEntity<>("Student successfully updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
