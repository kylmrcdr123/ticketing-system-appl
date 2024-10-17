package com.rocs.ticketing.system.service.ticket.impl;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.misStaff.MisStaff;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.domain.ticket.Ticket;
import com.rocs.ticketing.system.repository.employee.EmployeeRepository;
import com.rocs.ticketing.system.repository.student.StudentRepository;
import com.rocs.ticketing.system.repository.misStaff.MisStaffRepository;
import com.rocs.ticketing.system.repository.ticket.TicketRepository;
import com.rocs.ticketing.system.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final MisStaffRepository misStaffRepository;
    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public TicketServiceImpl(
            TicketRepository ticketRepository,
            MisStaffRepository misStaffRepository,
            EmployeeRepository employeeRepository,
            StudentRepository studentRepository) {

        this.ticketRepository = ticketRepository;
        this.misStaffRepository = misStaffRepository;
        this.employeeRepository = employeeRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public void updateStatus(Long ticketId, String status) {
        // Fetch the ticket from the repository
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id " + ticketId));

        // Update the status of the ticket
        ticket.setStatus(status);

        // If the status is "Done" or "Closed", update the dateFinished field
        if ("Done".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
            ticket.setDateFinished(new Date());  // Set the current date as dateFinished
        } else {
            ticket.setDateFinished(null);  // Reset the dateFinished if the status is not "Done" or "Closed"
        }

        // Save the updated ticket back to the repository
        ticketRepository.save(ticket);

        // Log the status update
        System.out.println("Ticket ID " + ticketId + " updated with status: " + status +
                (ticket.getDateFinished() != null ? ", Date Finished: " + ticket.getDateFinished() : ""));
    }

    @Override
    public Ticket addTicket(Ticket ticket) {
        // Require either Employee or Student to be set
        if (ticket.getEmployees() != null) {
            Employees employee = employeeRepository.findById(ticket.getEmployees().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + ticket.getEmployees().getId()));
            ticket.setEmployees(employee);
            ticket.setReporter("Employee");
        } else if (ticket.getStudents() != null) {
            Students student = studentRepository.findById(ticket.getStudents().getId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + ticket.getStudents().getId()));
            ticket.setStudents(student);
            ticket.setReporter("Student");
        } else {
            throw new RuntimeException("Either Employee or Student must be provided.");
        }

        // Allow MisStaff to be null when creating a ticket
        ticket.setMisStaff(null);

        return ticketRepository.save(ticket);
    }


    @Override
    public Ticket updateTicket(Ticket ticket) {
        Optional<Ticket> existingTicketOptional = ticketRepository.findById(ticket.getId());

        if (existingTicketOptional.isPresent()) {
            Ticket existingTicket = existingTicketOptional.get();

            // Log the incoming ticket details
            System.out.println("Incoming Ticket: " + ticket);

            // If misStaff is present, find and set the MisStaff object
            if (ticket.getMisStaff() != null && ticket.getMisStaff().getId() != null) {
                Optional<MisStaff> misStaffOptional = misStaffRepository.findById(ticket.getMisStaff().getId());
                if (misStaffOptional.isPresent()) {
                    existingTicket.setMisStaff(misStaffOptional.get());
                } else {
                    throw new RuntimeException("MisStaff with ID " + ticket.getMisStaff().getId() + " not found");
                }
            }

            existingTicket.setDateCreated(ticket.getDateCreated());
            existingTicket.setDateFinished(ticket.getDateFinished());
            existingTicket.setIssue(ticket.getIssue());
            existingTicket.setStatus(ticket.getStatus());
            existingTicket.setTicketNumber(ticket.getTicketNumber());

            // Save the existing ticket with the updated misStaff
            return ticketRepository.save(existingTicket);
        } else {
            throw new RuntimeException("Ticket with ID " + ticket.getId() + " not found");
        }
    }


    @Override
    public void assignTicket(Long ticketId, Long employeeId, Long studentId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id " + ticketId));

        if (employeeId != null) {
            Employees employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id " + employeeId));
            ticket.setEmployees(employee);
            ticket.setStudents(null); // Clear other assignments
            ticket.setMisStaff(null); // Clear other assignments
        } else if (studentId != null) {
            Students student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with id " + studentId));
            ticket.setStudents(student);
            ticket.setEmployees(null); // Clear other assignments
            ticket.setMisStaff(null); // Clear other assignments
        }

        // Log the assignment
        System.out.println("Ticket assigned to: " +
                (employeeId != null ? "Employee " + employeeId : "Student " + studentId));

        // Save the ticket
        ticketRepository.save(ticket);
    }

    @Override
    public void reassignTicket(Long ticketId, Long newMisStaffId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id " + ticketId));

        if (newMisStaffId != null) {
            MisStaff misStaff = misStaffRepository.findById(newMisStaffId)
                    .orElseThrow(() -> new RuntimeException("MIS Staff not found with id " + newMisStaffId));
            ticket.setMisStaff(misStaff);
        } else {
            // Allow MisStaff to be null, while Employee or Student remains set
            ticket.setMisStaff(null);
        }

        ticketRepository.save(ticket);
    }


    @Override
    public List<Ticket> getTicketByStatus(String status) {
        // Just pass the status as it is
        return ticketRepository.findByStatusIgnoreCase(status);
    }

    @Override
    public List<Ticket> getTicketsByDateRange(Date dateCreated, Date dateFinished) {
        return ticketRepository.findByDateRangeBetween(dateCreated, dateFinished);
    }

    @Override
    public List<Ticket> getAllTicketByMisStaffNumber(String misStaffNumber) {
        return ticketRepository.findByMisStaff_MisStaffNumber(misStaffNumber);
    }


    public boolean assignTicket(Long ticketId, Long misStaffId) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        Optional<MisStaff> staffOpt = misStaffRepository.findById(misStaffId);

        if (ticketOpt.isPresent() && staffOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            MisStaff misStaff = staffOpt.get();
            ticket.setMisStaff(misStaff);  // Assign the staff to the ticket
            ticketRepository.save(ticket);
            return true;
        }
        return false;  // Either ticket or MIS staff not found
    }


    @Override
    public List<Ticket> getAllTicketsByMisStaffName(String name) {
        return ticketRepository.findByMisStaffFirstNameContainingIgnoreCaseOrMisStaffMiddleNameContainingIgnoreCaseOrMisStaffLastNameContainingIgnoreCase(name, name, name);
    }

    // TicketServiceImpl.java
    @Override
    public List<Ticket> getTicketsByEmployeeNumber(String employeeNumber) {
        return ticketRepository.findByEmployees_EmployeeNumber(employeeNumber);
    }

    @Override
    public List<Ticket> getTicketsByStudentNumber(String studentNumber)  {
        return ticketRepository.findByStudents_StudentNumber(studentNumber);
    }
}
