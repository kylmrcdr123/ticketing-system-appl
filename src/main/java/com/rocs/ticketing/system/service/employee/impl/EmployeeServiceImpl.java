package com.rocs.ticketing.system.service.employee.impl;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.repository.employee.EmployeeRepository;
import com.rocs.ticketing.system.service.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employees> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employees addEmployee(Employees employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employees getEmployeeByEmployeeNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber);
    }

    @Override
    public Employees updateEmployee(String userId, Employees employee) {
        if (employee == null || userId == null) {
            throw new IllegalArgumentException("Employee or ID cannot be null");
        }

        Employees existingEmployee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found with user ID: " + userId));

        // Update the employee details
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setMiddleName(employee.getMiddleName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setAddress(employee.getAddress());
        existingEmployee.setContactNumber(employee.getContactNumber());

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public Employees getEmployeeByUserId(String userId) {
        return employeeRepository.findByUserId(userId).orElse(null);
    }

    @Override
    public Optional<Employees> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employees> findByEmployeeNumberOptional(String employeeNumber) {
        return employeeRepository.findOptionalByEmployeeNumber(employeeNumber); // Use renamed method
    }

}
