package com.rocs.ticketing.system.domain.ticket;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class Ticket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(length = 32)
    private String ticketNumber;
    private Date dateCreated;
    private Date dateFinished;
    @Column(length = 64)
    private String status;
    @Column(length = 300)
    private String issue;
    private String reporter;

    @ManyToOne
    @JoinColumn(name = "mis_staff_id", nullable = true) // Set nullable to true
    private MisStaff misStaff;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = true) // Set nullable to true to handle cases where the reporter is a student
    private Employees employee;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = true)  // Same logic as above
    private Students student;

}
