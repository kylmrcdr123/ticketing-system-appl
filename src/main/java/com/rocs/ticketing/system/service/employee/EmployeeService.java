package com.rocs.ticketing.system.service.employee;

import com.rocs.ticketing.system.domain.employees.Employees;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employees> getAllEmployees();  // Adjusted method name to plural for consistency
    Employees addEmployee(Employees employee);
    Employees getEmployeeByEmployeeNumber(String employeeNumber);
    Employees updateEmployee(String userId, Employees employee);
    Employees getEmployeeByUserId(String userId);
    Optional<Employees> findById(Long id);
   Optional<Employees> findByEmployeeNumberOptional(String employeeNumber); // Update service interface

}
