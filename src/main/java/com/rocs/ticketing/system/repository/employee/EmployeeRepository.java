package com.rocs.ticketing.system.repository.employee;

import com.rocs.ticketing.system.domain.employees.Employees;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employees, Long> {

    boolean existsByEmployeeNumberAndEmail(String employeeNumber, String email);

    Employees findByEmployeeNumber(String employeeNumber);

    boolean existsByEmployeeNumber(String userNumber);
}
