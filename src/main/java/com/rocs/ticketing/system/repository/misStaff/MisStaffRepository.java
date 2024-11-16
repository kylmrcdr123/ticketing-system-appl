package com.rocs.ticketing.system.repository.misStaff;


import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MisStaffRepository extends JpaRepository<MisStaff, Long> {
    MisStaff findByMisStaffNumber(String misStaffNumber);
    boolean existsByMisStaffNumberAndEmail(String misStaffNumber, String email);

    @Query("SELECT s FROM MisStaff s WHERE s.misStaffNumber = :userId")
    Optional<MisStaff> findByUserId(@Param("userId") String userId);

}


