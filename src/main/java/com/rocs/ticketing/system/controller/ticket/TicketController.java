package com.rocs.ticketing.system.controller.ticket;

import com.rocs.ticketing.system.domain.ticket.Ticket;
import com.rocs.ticketing.system.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/TicketService")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
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
            if (ticket.getMisStaff() == null) {
                return new ResponseEntity<>("MIS Staff is required", HttpStatus.BAD_REQUEST);
            }

            if (ticket.getEmployee() == null && ticket.getStudent() == null) {
                return new ResponseEntity<>("Either Employee or Student must be provided", HttpStatus.BAD_REQUEST);
            }

            ticketService.addTicket(ticket);
            return new ResponseEntity<>("Ticket successfully added", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Ticket cannot be added: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/updateStatus/{ticketId}")
    public ResponseEntity<Map<String, String>> updateTicketStatus(@PathVariable Long ticketId, @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");

        if (status == null || status.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Status is required"), HttpStatus.BAD_REQUEST);
        }

        try {
            ticketService.updateStatus(ticketId, status);
            return new ResponseEntity<>(Map.of("message", "Ticket status updated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Failed to update ticket: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/ticket/update/{ticketId}")
    public ResponseEntity<String> updateTicket(@PathVariable Long ticketId, @RequestBody Ticket ticketDetails) {
        try {
            ticketDetails.setTicketId(ticketId);
            Ticket updatedTicket = ticketService.updateTicket(ticketDetails);

            if (updatedTicket != null) {
                return new ResponseEntity<>("Ticket successfully updated", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ticket not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Update failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTicketsByStatus(@PathVariable String status) {
        try {
            // Normalize the status input to match the stored status format
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
    @GetMapping("/ticket/misStaff/name/{name}")
    public ResponseEntity<List<Ticket>> getAllTicketsByMisStaffName(@PathVariable String name) {
        return new ResponseEntity<>(ticketService.getAllTicketsByMisStaffName(name), HttpStatus.OK);
    }


}
