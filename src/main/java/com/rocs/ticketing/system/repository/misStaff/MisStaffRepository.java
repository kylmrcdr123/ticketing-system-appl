package com.rocs.ticketing.system.repository.misStaff;


import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MisStaffRepository extends JpaRepository<MisStaff, Long> {
    MisStaff findByMisStaffNumber(String misStaffNumber);
    boolean existsByMisStaffNumberAndEmail(String misStaffNumber, String email);
    MisStaff findByUser_Id(long id);
    boolean existsByMisStaffNumber(String misStaffNumber);

}


