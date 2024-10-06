package com.rocs.ticketing.system.service.misStaff.impl;


import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.repository.misStaff.MisStaffRepository;
import com.rocs.ticketing.system.service.misStaff.MisStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MisStaffServiceImpl implements MisStaffService {

    private MisStaffRepository misStaffRepository;

    @Autowired
    public MisStaffServiceImpl(MisStaffRepository misStaffRepository) {
        this.misStaffRepository = misStaffRepository;
    }

    @Override
    public List<MisStaff> findAllMisStaff() {
        return misStaffRepository.findAll();
    }

    @Override
    public MisStaff addMisStaff(MisStaff misStaff) {
        return misStaffRepository.save(misStaff);
    }


    @Override
    public MisStaff getMisStaffByNumber(String misStaffNumber) {
        return misStaffRepository.findByMisStaffNumber(misStaffNumber);
    }

    @Override
    public MisStaff updateMisStaff(Long id, MisStaff misStaff) {
        if (misStaff == null || id == null) {
            throw new IllegalArgumentException("MisStaff or ID cannot be null");
        }

        MisStaff existingMisStaff = misStaffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MisStaff not found with ID: " + id));

        // Update fields with new data
        existingMisStaff.setFirstName(misStaff.getFirstName());
        existingMisStaff.setMiddleName(misStaff.getMiddleName());
        existingMisStaff.setLastName(misStaff.getLastName());
        existingMisStaff.setEmail(misStaff.getEmail());
        existingMisStaff.setAddress(misStaff.getAddress());
        existingMisStaff.setContactNumber(misStaff.getContactNumber());
        existingMisStaff.setDateCreated(misStaff.getDateCreated());  // Also update the dateCreated if needed
        existingMisStaff.setMisStaffNumber(misStaff.getMisStaffNumber());  // Also update the dateCreated if needed

        return misStaffRepository.save(existingMisStaff);
    }

    @Override
    public Optional<MisStaff> getMisStaffById(Long id) {
        return misStaffRepository.findById(id);
    }

}
