package com.rocs.ticketing.system.repository.employee;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.student.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employees, Long> {

    boolean existsByEmployeeNumberAndEmail(String employeeNumber, String email);

    Employees findByEmployeeNumber(String employeeNumber);

    boolean existsByEmployeeNumber(String userNumber);

    @Query("SELECT s FROM Employees s WHERE s.employeeNumber = :userId")
    Optional<Employees> findByUserId(@Param("userId") String userId);

    Optional<Employees> findOptionalByEmployeeNumber(String employeeNumber); // Renamed method
}
