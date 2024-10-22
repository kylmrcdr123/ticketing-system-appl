package com.rocs.ticketing.system.service.student.impl;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.repository.student.StudentRepository;
import com.rocs.ticketing.system.service.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Students> getAllStudent() {
        return studentRepository.findAll();
    }

    @Override
    public Students addStudent(Students student) {
        return studentRepository.save(student);
    }

    @Override
    public Students getStudentByNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    @Override
    public Students updateStudents(String userId, Students student) {
        if (student == null || userId == null) {
            throw new IllegalArgumentException("Student or ID cannot be null");
        }

        Students existingStudent = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with user ID: " + userId));

        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setMiddleName(student.getMiddleName());
        existingStudent.setLastName(student.getLastName());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setAddress(student.getAddress());
        existingStudent.setContactNumber(student.getContactNumber());

        return studentRepository.save(existingStudent);
    }

    @Override
    public Students getStudentByUserId(String userId) {
        return studentRepository.findByUserId(userId).orElse(null);
    }


    @Override
    public Optional<Students> findById(Long id) {
        return studentRepository.findById(id);
    }




}
