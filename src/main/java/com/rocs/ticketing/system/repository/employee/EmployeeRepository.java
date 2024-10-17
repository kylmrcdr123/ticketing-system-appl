package com.rocs.ticketing.system.repository.employee;

import com.rocs.ticketing.system.domain.employees.Employees;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employees, Long> {

    boolean existsByEmployeeNumberAndEmail(String employeeNumber, String email);

    Employees findByEmployeeNumber(String employeeNumber);

    boolean existsByEmployeeNumber(String userNumber);
    Employees findByUserId(Long userId);

    Optional<Employees> findOptionalByEmployeeNumber(String employeeNumber); // Renamed method

    Employees findByUser_Id(long id);

}
