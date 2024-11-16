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
    public Ticket addTicket(Ticket ticket) {
        // Check if either employee or student is provided
        if (ticket.getEmployee() != null) {
            Employees employee = employeeRepository.findById(ticket.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + ticket.getEmployee().getId()));
            ticket.setEmployee(employee);  // Set the fetched Employee in the Ticket
            // Set reporter information
            ticket.setReporter("Employee"); // Set reporter as employee
        } else if (ticket.getStudent() != null) {
            Students student = studentRepository.findById(ticket.getStudent().getId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + ticket.getStudent().getId()));
            ticket.setStudent(student);  // Set the fetched Student in the Ticket
            // Set reporter information
            ticket.setReporter("Student"); // Set reporter as student
        } else {
            throw new RuntimeException("Either Employee or Student must be provided.");
        }

        // If misStaff is not provided, set it to null
        ticket.setMisStaff(null); // Explicitly set MIS staff to null

        return ticketRepository.save(ticket);  // Save the ticket
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        Optional<Ticket> existingTicket = ticketRepository.findById(ticket.getTicketId());
        if (existingTicket.isPresent()) {
            // Get the existing ticket
            Ticket ticketToUpdate = existingTicket.get();

            // Update the status if provided
            if (ticket.getStatus() != null) {
                // If the status is 'Done' or 'Closed', set the dateFinished field
                if ("Done".equals(ticket.getStatus()) || "Closed".equals(ticket.getStatus())) {
                    // Set the current date and time as the dateFinished
                    ticketToUpdate.setDateFinished(new Date());  // Using Date to represent the current date/time
                }
                ticketToUpdate.setStatus(ticket.getStatus());
            }

            // Optionally update the issue if provided
            if (ticket.getIssue() != null) {
                ticketToUpdate.setIssue(ticket.getIssue());
            }

            // Optionally update the student if provided
            if (ticket.getStudent() != null) {
                ticketToUpdate.setStudent(ticket.getStudent());
            }

            // Optionally update the employee (assignee) if provided
            if (ticket.getEmployee() != null) {
                ticketToUpdate.setEmployee(ticket.getEmployee());
            }

            // Update the MIS staff if provided
            if (ticket.getMisStaff() != null && ticket.getMisStaff().getId() != null) {
                ticketToUpdate.setMisStaff(ticket.getMisStaff());
            }

            // You can add similar checks for other fields if necessary

            // Save and return the updated ticket
            return ticketRepository.save(ticketToUpdate);
        }
        return null; // Ticket not found
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


    // TicketServiceImpl.java
    @Override
    public List<Ticket> getTicketsByEmployeeNumber(String employeeNumber) {
        return ticketRepository.findByEmployee_EmployeeNumber(employeeNumber);
    }

    @Override
    public List<Ticket> getTicketsByStudentNumber(String studentNumber)  {
        return ticketRepository.findByStudent_StudentNumber(studentNumber);
    }
}
