package com.rocs.ticketing.system.service.misStaff;

import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;

import java.util.List;
import java.util.Optional;

public interface MisStaffService {
    List<MisStaff> findAllMisStaff();
    MisStaff addMisStaff(MisStaff misStaff);
    MisStaff getMisStaffByNumber(String misStaffNumber);
    MisStaff updateMisStaff(Long id, MisStaff misStaff);
    Optional<MisStaff> getMisStaffById(Long id);
    MisStaff getMisStaffByUserId(String userId);
}

