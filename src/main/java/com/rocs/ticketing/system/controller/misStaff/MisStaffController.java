package com.rocs.ticketing.system.controller.misStaff;

import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.service.misStaff.MisStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/MisStaffService")
public class MisStaffController {
        private final MisStaffService misStaffService;

        @Autowired
        public MisStaffController(MisStaffService misStaffService) {
            this.misStaffService = misStaffService;
        }

    @GetMapping("/staff")
    public ResponseEntity<List<MisStaff>> getAllMisStaff() {
        List<MisStaff> staffList = misStaffService.findAllMisStaff(); // Replace with your service logic
        return new ResponseEntity<>(staffList, HttpStatus.OK);
    }


    @PostMapping("/staff/add")
        public ResponseEntity<String> addMisStaff(@RequestBody MisStaff misStaff) {
            try {
                MisStaff newMisStaff= misStaffService.addMisStaff(misStaff);
                return new ResponseEntity<>("MisStaff successfully added", HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>("MisStaff cannot be added", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    @PutMapping("/staff/update/{misId}")
    public ResponseEntity<String> updateMisStaff(@PathVariable("misId") Long id, @RequestBody MisStaff misStaff) {
        try {
            MisStaff updatedMisStaff = misStaffService.updateMisStaff(id, misStaff);
            if (updatedMisStaff != null) {
                return new ResponseEntity<>("MisStaff successfully updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("MisStaff not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/misStaff/{id}")
    public ResponseEntity<Optional<MisStaff>> getMisStaffById(@PathVariable Long id) {
        return new ResponseEntity<>(this.misStaffService.getMisStaffById(id), HttpStatus.OK);
    }

    @GetMapping("/misStaff/{userId}")
    public ResponseEntity<MisStaff> getMisStaffByUserId(@PathVariable("userId") String userId) {
        MisStaff misStaff = misStaffService.getMisStaffByUserId(userId);
        if (misStaff != null) {
            return new ResponseEntity<>(misStaff, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

