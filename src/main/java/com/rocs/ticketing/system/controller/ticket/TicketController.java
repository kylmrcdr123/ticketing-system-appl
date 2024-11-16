package com.rocs.ticketing.system.controller.ticket;

import com.rocs.ticketing.system.domain.employees.Employees;
import com.rocs.ticketing.system.domain.student.Students;
import com.rocs.ticketing.system.domain.ticket.Ticket;
import com.rocs.ticketing.system.service.employee.EmployeeService;
import com.rocs.ticketing.system.service.student.StudentService;
import com.rocs.ticketing.system.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/TicketService")
public class TicketController {

    private final TicketService ticketService;
    private final StudentService studentService;
    private final EmployeeService employeeService;

    @Autowired
    public TicketController(TicketService ticketService, StudentService studentService, EmployeeService employeeService) {
        this.ticketService = ticketService;
        this.studentService = studentService;
        this.employeeService = employeeService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id)
                .map(ticket -> new ResponseEntity<>(ticket, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/ticket/add")
    public ResponseEntity<String> addTicket(@RequestBody Ticket ticket) {
        try {
            // If student is provided, check for existence and set
            if (ticket.getStudent() != null) {
                Students student = studentService.getStudentByNumber(ticket.getStudent().getStudentNumber());
                if (student == null) {
                    return new ResponseEntity<>("Student not found with student number: " + ticket.getStudent().getStudentNumber(), HttpStatus.NOT_FOUND);
                }
                ticket.setStudent(student); // Set the student on the ticket
            }

            // If employee is provided, check for existence and set
            if (ticket.getEmployee() != null) {
                Employees employee = employeeService.findByEmployeeNumberOptional(ticket.getEmployee().getEmployeeNumber())
                        .orElseThrow(() -> new RuntimeException("Employee not found with employee number: " + ticket.getEmployee().getEmployeeNumber()));
                ticket.setEmployee(employee); // Set the employee on the ticket
            }

            // No need to handle MIS staff; just add the ticket
            ticketService.addTicket(ticket);
            return new ResponseEntity<>("Ticket successfully added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Ticket cannot be added: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTicketsByStatus(@PathVariable String status) {
        try {
            String normalizedStatus = status.trim().toLowerCase();
            List<Ticket> tickets = ticketService.getTicketByStatus(normalizedStatus);
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to retrieve tickets: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Ticket>> getTicketsByDateRange(
            @RequestParam("dateCreated") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateCreated,
            @RequestParam("dateFinished") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFinished) {

        return new ResponseEntity<>(ticketService.getTicketsByDateRange(dateCreated, dateFinished), HttpStatus.OK);
    }

    @GetMapping("/misStaff/tickets/{misStaffNumber}")
    public ResponseEntity<List<Ticket>> getTicketsByMisStaffNumber(@PathVariable String misStaffNumber) {
        List<Ticket> tickets = ticketService.getAllTicketByMisStaffNumber(misStaffNumber);
        if (tickets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PutMapping("/assign/{ticketId}")
    public ResponseEntity<String> assignOrReassignTicket(
            @PathVariable Long ticketId,
            @RequestBody Map<String, Long> requestBody) {

        Long misStaffId = requestBody.get("misStaffId");

        if (misStaffId == null) {
            return new ResponseEntity<>("MIS Staff ID is required", HttpStatus.BAD_REQUEST);
        }

        try {
            boolean success = ticketService.assignTicket(ticketId, misStaffId);
            if (success) {
                return new ResponseEntity<>("Ticket assigned successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ticket or staff not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to assign ticket: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/tickets/user/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsForUser(@PathVariable String userId) {
        List<Ticket> tickets;

        if (userId.startsWith("CT")) {
            tickets = ticketService.getTicketsByStudentNumber(userId);
        } else if (userId.length() == 8 && userId.matches("\\d+")) {
            tickets = ticketService.getTicketsByEmployeeNumber(userId);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (tickets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
    @PutMapping("/ticket/update")
    public ResponseEntity<String> updateTicket(@RequestBody Ticket ticket) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(ticket);
            if (updatedTicket != null) {
                return new ResponseEntity<>("Ticket successfully updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ticket not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update ticket: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
