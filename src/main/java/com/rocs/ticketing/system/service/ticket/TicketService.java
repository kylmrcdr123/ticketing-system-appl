package com.rocs.ticketing.system.service.ticket;

import com.rocs.ticketing.system.domain.ticket.Ticket;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TicketService {
 List<Ticket> getAllTickets();
 Optional<Ticket> getTicketById(Long id);
 Ticket addTicket(Ticket ticket);
 Ticket updateTicket(Ticket ticket);
 List<Ticket> getTicketByStatus(String status);
 List<Ticket> getTicketsByDateRange(Date dateCreated, Date dateFinished);
 List<Ticket> getAllTicketByMisStaffNumber(String misStaffNumber);
 boolean assignTicket(Long ticketId, Long misStaffId);
 List<Ticket> getTicketsByStudentNumber(String studentNumber);
 List<Ticket> getTicketsByEmployeeNumber(String employeeNumber);
}
