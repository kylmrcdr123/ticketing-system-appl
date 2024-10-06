package com.rocs.ticketing.system.service.employee.impl;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.repository.employee.EmployeeRepository;
import com.rocs.ticketing.system.service.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Employees updateEmployee(Long id, Employees employee) {
        if (employee == null || id == null) {
            throw new IllegalArgumentException("Employee or ID cannot be null");
        }

        Employees existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));

        // Update fields with new data
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setMiddleName(employee.getMiddleName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setAddress(employee.getAddress());
        existingEmployee.setContactNumber(employee.getContactNumber());
        existingEmployee.setDateCreated(employee.getDateCreated());  // Update dateCreated if needed

        return employeeRepository.save(existingEmployee);
    }


}
