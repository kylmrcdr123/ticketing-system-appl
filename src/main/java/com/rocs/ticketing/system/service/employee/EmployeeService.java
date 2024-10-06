package com.rocs.ticketing.system.service.employee;

import com.rocs.ticketing.system.domain.employees.Employees;

import java.util.List;

public interface EmployeeService {
    List<Employees> getAllEmployees();  // Adjusted method name to plural for consistency
    Employees addEmployee(Employees employee);
    Employees getEmployeeByEmployeeNumber(String employeeNumber);
    Employees updateEmployee(Long id, Employees employee);

}
